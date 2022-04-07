from django.db import models
from django.db.models.fields import CharField
from django.db.models.fields.files import FileField
from authentication.models import User
from helpers.utils import Util


class Tags(models.Model):
    tag = models.CharField(max_length=50)

    def __str__(self) -> str:
        return self.tag


class Content(models.Model):
    CHOICES_TYPE = [
        ("Audio", "Audio"),
        ("Video", "Video"),
    ]

    CHOICES_CATEGORY = [
        ("Pyschology", "Psychology"),
        ("Tutorial", "Tutorial"),
        ("Movie", "Movie"),
        ("Music", "Music"),
        ("Audio Book", "Audio Book"),
    ]

    id = models.UUIDField(primary_key=True, db_index=True)
    name = models.CharField(max_length=255, null=False, blank=False)
    size = models.BigIntegerField(null=False, blank=False)
    length = models.IntegerField(default=0)
    description = models.TextField(blank=False, null=False)
    price = models.DecimalField(max_digits=10, decimal_places=2, default=0)
    # todo: change key to hashed
    key = CharField(max_length=16, blank=False, null=False)
    nonce = CharField(max_length=12, blank=False, null=False)
    type = models.CharField(
        max_length=25, choices=CHOICES_TYPE, blank=False, null=False
    )
    category = models.CharField(
        max_length=25, choices=CHOICES_CATEGORY, blank=False, null=False
    )
    tags = models.ManyToManyField(to=Tags)
    image = models.FileField(null=True)
    upload = models.FileField(null=True)
    content = models.FileField(null=True)
    preview = models.FileField(null=True, blank=True)
    owner = models.ForeignKey(to=User, on_delete=models.DO_NOTHING)
    likes = models.IntegerField(default=0)
    views = models.IntegerField(default=0)
    is_finished = models.BooleanField(default=False)
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)

    def __str__(self) -> str:
        return self.name


class Like(models.Model):
    owner = models.ForeignKey(to=User, on_delete=models.DO_NOTHING)
    content = models.ForeignKey(to=Content, on_delete=models.DO_NOTHING)
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)

    def __str__(self) -> str:
        return self.content.name
