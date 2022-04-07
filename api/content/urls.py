from django.urls import path
from .views import *


urlpatterns = [
    path('popular/', PopularContentsApiView.as_view(), name="contents"),
    path('new/', NewContentsApiView.as_view(), name="contents"),
    path('get/<str:id>', GetContentApiView.as_view(), name="get-content"),
    path('upload/', UploadContentApiView.as_view(), name="upload-content"),
    path('edit/<str:id>', EditContentApiView.as_view(), name="edit-content"),
    path('search/', SearchContentApiView.as_view(), name="search-content"),
    path('like/', LikeContentApiView.as_view(), name="like-content"),
]
