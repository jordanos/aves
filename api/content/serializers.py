from django.core.validators import DecimalValidator
from django.db import models
from rest_framework import generics, serializers
from rest_framework.fields import ImageField
from .models import Content, Like
from authentication.models import User


class ContentSerializer(serializers.ModelSerializer):
    '''
    A serializer for content
    '''
    id = serializers.UUIDField(read_only=True)
    name = serializers.CharField(max_length=50, min_length=3)
    # size = serializers.IntegerField(read_only=True)
    owner = serializers.PrimaryKeyRelatedField(
        read_only=True)
    likes = serializers.IntegerField(read_only=True)
    views = serializers.IntegerField(read_only=True)
    image = serializers.ImageField(read_only=True)
    content = serializers.CharField(read_only=True)
    preview = serializers.CharField(read_only=True)
    is_finished = serializers.CharField(read_only=True)

    class Meta:
        model = Content
        fields = ['id', 'name', 'size', 'description',
                  'price', 'type', 'category', 'image', 'content', 'preview', 'likes', 'views', 'owner', 'tags', 'is_finished']


class UploadSerializer(serializers.ModelSerializer):
    id = serializers.UUIDField(read_only=True)

    class Meta:
        model = Content
        fields = ['id']

    def validate(self, attrs):
        return attrs

    def save(self, **kwargs):
        return super().save(**kwargs)


class EditSerializer(serializers.ModelSerializer):
    id = serializers.UUIDField(read_only=True)

    class Meta:
        model = Content
        fields = ['id', 'name', 'description', 'price', 'category']

    def validate(self, attrs):
        return attrs

    def save(self, **kwargs):
        return super().save(**kwargs)


class LikeSerializer(serializers.ModelSerializer):
    owner = serializers.PrimaryKeyRelatedField(queryset=User.objects.all())
    content = serializers.PrimaryKeyRelatedField(
        queryset=Content.objects.all())

    class Meta:
        model = Like
        fields = ['owner', 'content']

    def validate(self, attrs):
        owner = attrs["owner"]
        content = attrs['content']
        return attrs

    def create(self, validated_data):
        return super().create(validated_data)
