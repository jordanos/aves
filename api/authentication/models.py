from django.db import models
from django.contrib.auth.models import (
    AbstractBaseUser,
    BaseUserManager,
    PermissionsMixin,
)
from rest_framework_simplejwt.tokens import RefreshToken


class UserManager(BaseUserManager):
    """
    Custom user functions
    """

    def create_user(self, username, email, password=None):
        # Overrides the django create user method, creates custom user when called
        if username is None:
            raise TypeError("No username provided")
        if email is None:
            raise TypeError("No email provided")

        user = self.model(
            username=username, email=self.normalize_email(email), is_verified=True
        )
        user.set_password(password)
        user.save()
        return user

    def create_superuser(self, username, email, password=None):
        # Overrides the django create super user method, creates cutom superuser when called
        if password is None:
            raise TypeError("No password provided")
        if email is None:
            raise TypeError("No email provided")

        user = self.create_user(username, email, password)
        user.is_superuser = True
        user.is_staff = True
        user.save()
        return user


class User(AbstractBaseUser, PermissionsMixin):
    """
    Custom user model fields extending from the django user model
    """

    username = models.CharField(max_length=50, unique=True, db_index=True)
    email = models.CharField(max_length=50, unique=True, db_index=True)
    is_verified = models.BooleanField(default=False)
    is_active = models.BooleanField(default=True)
    is_staff = models.BooleanField(default=False)
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)
    phone = models.BigIntegerField(null=True, blank=True)
    uploads = models.IntegerField(default=0)
    views = models.IntegerField(default=0)
    likes = models.IntegerField(default=0)
    balance = models.DecimalField(max_digits=10, default=0, decimal_places=2)
    image = models.ImageField(null=True, blank=True)

    USERNAME_FIELD = "email"
    REQUIRED_FIELDS = ["username"]

    objects = UserManager()

    def __str__(self) -> str:
        return self.username

    def tokens(self):
        refresh = RefreshToken.for_user(self)
        return {"refresh": str(refresh), "access": str(refresh.access_token)}
