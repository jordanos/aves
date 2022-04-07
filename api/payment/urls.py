from django.urls import path
from .views import *


urlpatterns = [
    path('create/', CreatePurchaseApiView.as_view(), name="create-purchase"),
    path('check/', CheckPurchaseApiView.as_view(), name="get-purchase"),
    path('info/<str:id>', InfoPurchaseApiView.as_view(), name="info-purchase"),
]
