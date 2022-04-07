from django.urls import path
from .views import *
from rest_framework_simplejwt.views import (
    TokenRefreshView,
)


urlpatterns = [
    path('create/', RegisterApiView.as_view(), name="create"),
    path('email-verify/', VerifyEmailApiView.as_view(), name="email-verify"),
    path('login/', LoginApiView.as_view(), name="login"),
    path('token/refresh/', TokenRefreshView.as_view(), name="token-refresh"),
    path('logout/', LogoutApiView.as_view(), name="logout"),
]
