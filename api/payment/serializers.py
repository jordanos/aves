from django.core.validators import DecimalValidator
from django.db import models
from rest_framework import generics, serializers
from rest_framework.fields import ImageField
from content.models import Content
from authentication.models import User
from .models import Purchase, Withdraw


class CreatePurchaseSerializer(serializers.ModelSerializer):
    """
    A serializer for purchasing
    """

    id = serializers.UUIDField(read_only=True)

    class Meta:
        model = Purchase
        fields = ["id", "owner", "content", "payment_method"]

    def validate(self, attrs):
        return super().validate(attrs)


class CheckPurchaseSerializer(serializers.ModelSerializer):
    """
    A serializer for purchasing
    """

    id = serializers.UUIDField(read_only=True)
    owner = serializers.PrimaryKeyRelatedField(queryset=User.objects.all())
    payment_method = serializers.CharField(read_only=True)

    class Meta:
        model = Purchase
        fields = ["id", "owner", "content", "payment_method"]


class GetPurchaseSerializer(serializers.ModelSerializer):
    """
    A serializer for purchasing
    """

    id = serializers.UUIDField(read_only=True)

    class Meta:
        model = Purchase
        fields = ["id", "owner", "content", "payment_method"]

    def validate(self, attrs):
        return super().validate(attrs)


class InfoPurchaseSerializer(serializers.ModelSerializer):
    """
    A serializer for purchasing
    """

    key = serializers.CharField(read_only=True)
    nonce = serializers.CharField(read_only=True)
    content = serializers.CharField(read_only=True)

    class Meta:
        model = Content
        fields = ["id", "key", "nonce", "content"]

    def validate(self, attrs):
        return super().validate(attrs)
