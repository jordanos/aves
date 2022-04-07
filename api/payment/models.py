from re import purge
from django.db import models
from authentication.models import User
from content.models import Content

METHODS = [
    ("Yenepay", "Yenepay")
]


# class Method(models.Model):
#     name = models.CharField(max_length=50)
#     created_at = models.DateTimeField(auto_now_add=True)
#     updated_at = models.DateTimeField(auto_now=True)


# class Deposit(models.Model):
#     id = models.UUIDField(primary_key=True)
#     owner = models.ForeignKey(to=User, on_delete=models.DO_NOTHING)
#     payment_method = models.ForeignKey(to=Method, on_delete=models.DO_NOTHING)
#     created_at = models.DateTimeField(auto_now_add=True)
#     updated_at = models.DateTimeField(auto_now=True)


class Withdraw(models.Model):
    id = models.UUIDField(primary_key=True)
    owner = models.ForeignKey(to=User, on_delete=models.DO_NOTHING)
    payment_method = models.CharField(max_length=50, choices=METHODS)
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)

    def __str__(self) -> str:
        return self.owner


class Purchase(models.Model):
    id = models.UUIDField(primary_key=True)
    owner = models.ForeignKey(to=User, on_delete=models.DO_NOTHING)
    content = models.ForeignKey(
        to=Content, on_delete=models.DO_NOTHING)
    payment_method = models.CharField(max_length=50, choices=METHODS)
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)

    def __str__(self) -> str:
        return self.content.name
