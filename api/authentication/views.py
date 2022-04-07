from django.core.mail.message import EmailMessage
from rest_framework import generics, status, views
from rest_framework.permissions import IsAuthenticated
from rest_framework.response import Response
from .serializer import (
    RegisterSerializer,
    VerifyEmailSerializer,
    LoginSerializer,
    LogoutSerializer,
)
from rest_framework_simplejwt.tokens import RefreshToken
from .models import User
from helpers.utils import Util
from django.contrib.sites.shortcuts import get_current_site
from django.urls import reverse
from django.conf import settings
import jwt
from drf_yasg.utils import swagger_auto_schema
from drf_yasg import openapi


class RegisterApiView(generics.GenericAPIView):
    """
    Api endpoint to register new users
    """

    serializer_class = RegisterSerializer

    def post(self, request):

        user = request.data
        serializer = self.serializer_class(data=user)
        serializer.is_valid(raise_exception=True)
        serializer.save()
        user_data = serializer.data

        user = User.objects.get(email=user_data["email"])
        token = RefreshToken.for_user(user)

        current_site = get_current_site(request).domain
        relative_url = reverse("email-verify")
        abs_url = "http://" + current_site + relative_url + "?token=" + str(token)
        data = {
            "subject": "Verify your email",
            "body": f"Dear {user.username}\nPlease verify your email using the link bellow.\n{abs_url}",
            "to": [user.email],
        }

        # Util.send_email(data)
        return Response(user_data, status.HTTP_201_CREATED)


class VerifyEmailApiView(views.APIView):
    """
    Api endpoint for accepting verification of emails
    """

    serializer_class = VerifyEmailSerializer
    token_param_config = openapi.Parameter(
        "token",
        in_=openapi.IN_QUERY,
        descrption="Description",
        type=openapi.TYPE_STRING,
    )

    @swagger_auto_schema(manual_parameters=[token_param_config])
    def get(self, request):
        token = request.GET.get("token")
        try:
            # decode token using jwt to get the user id and update user's verified field
            payload = jwt.decode(token, settings.SECRET_KEY)
            user = User.objects.get(id=payload["user_id"])
            if not user.is_verified:
                user.is_verified = True
                user.save()
            return Response({"email": user.email}, status.HTTP_200_OK)
        except jwt.ExpiredSignitureError as e:
            return Response(
                {"error": "Activation link expired"}, status.HTTP_400_BAD_REQUEST
            )


class LoginApiView(generics.GenericAPIView):
    """
    Api endpoint to login users
    """

    serializer_class = LoginSerializer

    def post(self, request):
        serializer = self.serializer_class(data=request.data)
        serializer.is_valid(raise_exception=True)
        return Response(serializer.data, status.HTTP_200_OK)


class LogoutApiView(generics.GenericAPIView):
    """
    Api endpoint to logout users
    """

    serializer_class = LogoutSerializer
    permission_classes = (IsAuthenticated,)

    def post(self, request):
        serializer = self.serializer_class(data=request.data)
        serializer.is_valid(raise_exception=True)
        serializer.save()
        return Response({}, status.HTTP_204_NO_CONTENT)
