from os import write
from django.db import models
from django.db.models import fields
from django.contrib import auth
from rest_framework import serializers
from rest_framework.exceptions import AuthenticationFailed
from .models import User
from rest_framework_simplejwt.tokens import TokenError, RefreshToken
import json


class RegisterSerializer(serializers.ModelSerializer):
    password = serializers.CharField(
        max_length=128, min_length=6, write_only=True)
    id = serializers.IntegerField(read_only=True)

    class Meta:
        model = User
        fields = ['id', 'username', 'email', 'password']

    def validate(self, attrs):
        username = attrs.get('username', '')
        email = attrs.get('email', '')

        if not username.isalnum():
            raise serializers.ValidationError('Email field error')
        return attrs

    def create(self, validated_data):
        return User.objects.create_user(**validated_data)


class VerifyEmailSerializer(serializers.ModelSerializer):
    token = serializers.CharField(max_length=555)

    class Meta:
        model = User
        fields = ['token']


class LoginSerializer(serializers.ModelSerializer):
    id = serializers.IntegerField(read_only=True)
    email = serializers.CharField(max_length=50, min_length=5)
    password = serializers.CharField(
        max_length=68, min_length=6, write_only=True)
    username = serializers.CharField(max_length=50, read_only=True)
    refreshToken = serializers.CharField(read_only=True)
    accessToken = serializers.CharField(read_only=True)

    class Meta:
        model = User
        fields = ['id', 'username', 'email', 'password',
                  'refreshToken', 'accessToken']

    def validate(self, attrs):
        email = attrs.get('email', '')
        password = attrs.get('password', '')

        user = auth.authenticate(email=email, password=password)
        if not user:
            raise AuthenticationFailed('Invalid credentials')
        if not user.is_active:
            raise AuthenticationFailed(
                'Account disabled, please contact admin')
        if not user.is_verified:
            raise AuthenticationFailed('Email not verified')

        user_tokens = user.tokens()
        return {
            'id': user.id,
            'email': user.email,
            'username': user.username,
            'refreshToken': user_tokens["refresh"],
            'accessToken': user_tokens["access"]
        }


class LogoutSerializer(serializers.Serializer):
    refresh = serializers.CharField()

    def validate(self, attrs):
        self.refresh = attrs['refresh']
        return attrs

    def save(self):
        try:
            RefreshToken(self.refresh).blacklist()
        except TokenError:
            self.fail('Bad token')
