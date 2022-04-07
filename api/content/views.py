import re
from django.core.files.uploadedfile import UploadedFile
from django.http import request
from rest_framework import generics, permissions, serializers, status
from rest_framework.response import Response

# from rest_framework import response
from rest_framework.views import APIView
from .serializers import (
    ContentSerializer,
    EditSerializer,
    UploadSerializer,
    LikeSerializer,
)
from helpers.utils import Util
from .content_helpers import ContentThread
from .models import Content, Like
from authentication.models import User
from .permissions import IsOwner
from rest_framework.parsers import MultiPartParser, FormParser


class CreateContentApiView(generics.CreateAPIView):
    """
    Api endpoint to create contents
    """

    serializer_class = ContentSerializer
    permission_classes = (permissions.IsAuthenticated,)

    def perform_create(self, serializer):
        return serializer.save(owner=self.request.user, is_finished=True)


class PopularContentsApiView(generics.ListAPIView):
    """
    Api endpoint to get contents
    """

    serializer_class = ContentSerializer
    queryset = Content.objects.order_by("-likes")

    def get_queryset(self):
        self.permission_classes = ()
        return self.queryset.filter()


class NewContentsApiView(generics.ListAPIView):
    """
    Api endpoint to get contents
    """

    serializer_class = ContentSerializer
    queryset = Content.objects.order_by("-created_at")

    def get_queryset(self):
        self.permission_classes = ()
        return self.queryset.filter()


class GetContentApiView(generics.RetrieveAPIView):
    """
    Api endpoint to fet contents using content uuid
    """

    serializer_class = ContentSerializer
    queryset = Content.objects.all()
    lookup_field = "id"

    def get_queryset(self):
        return self.queryset.filter()


# class GetContentApiView(generics.RetrieveAPIView):
#     '''
#     Api endpoint to edit contents using owner(user) and perform update, delete
#     '''
#     serializer_class = ContentSerializer
#     queryset = Content.objects.all()
#     permission_classes = (permissions.IsAuthenticated, IsOwner,)
#     lookup_field = 'id'

#     def perform_create(self, serializer):
#         return serializer.save(id=Util.create_uuid(), owner=self.request.user)

#     def get_queryset(self):
#         return self.queryset.filter()


class UploadContentApiView(APIView):
    """
    Api endpoint to upload content data(file, image)
    """

    parser_classes = (
        MultiPartParser,
        FormParser,
    )
    # permission_classes = (permissions.IsAuthenticated,)

    def post(self, request, format=None, *args, **kwargs):
        data = request._request.POST
        files = request._request.FILES
        base = Util.get_media_base()

        image = files["image"]
        upload = files["upload"]

        uid = Util.create_uuid()
        name = data["name"]
        description = data["description"]
        category = data["category"]
        price = data["price"]

        file_type = Util.get_filetype(upload.name)
        image_loc = f"/contents/image/{str(uid)}{image.name}"
        upload_loc = f"/contents/upload/{str(uid)}.{file_type}"

        content = Content.objects.create(
            id=uid,
            name=name,
            size=upload.size,
            description=description,
            price=price,
            key="",
            nonce="",
            type="Video",
            category=category,
            image=image_loc,
            upload=upload_loc,
            owner=User.objects.get(pk=1),
        )
        content.save()

        # Save image of the uploaded content
        with open(base + image_loc, "wb") as file:
            for chunk in image.chunks():
                file.write(chunk)

        # save the uploaded content
        with open(base + upload_loc, "wb") as file:
            for chunk in upload.chunks():
                file.write(chunk)

        content_thread = ContentThread(uid, upload_loc)
        content_thread.start()

        return Response({"detail": True}, status.HTTP_201_CREATED)


class EditContentApiView(generics.UpdateAPIView):
    """
    Api endpoint to edit contents useing id
    """

    serializer_class = EditSerializer
    queryset = Content.objects.all()
    # permission_classes = (permissions.IsAuthenticated, IsOwner,)
    lookup_field = "id"

    def perform_create(self, serializer):
        return serializer.save()


class SearchContentApiView(generics.ListAPIView):
    """
    Api endpoint to get search queries
    """

    serializer_class = ContentSerializer
    queryset = Content.objects.all()

    def get_queryset(self):
        return self.queryset.filter(name__contains=self.request.query_params["q"])


class LikeContentApiView(generics.GenericAPIView):
    """
    Api endpoint like content
    """

    serializer_class = LikeSerializer
    # permission_classes = (permissions.IsAuthenticated,)

    def post(self, request):
        """
        accept likes
        """
        serializer = self.serializer_class(data=request.data)
        serializer.is_valid()

        validated_data = serializer.data
        user = User.objects.get(id=validated_data["owner"])
        content = Content.objects.get(id=validated_data["content"])
        num_results = Like.objects.filter(owner=user.id).count()
        if num_results == 0:
            content.likes += 1
            content.save()
            user.likes += 1
            user.save()
            like = Like.objects.create(owner=user, content=content)
            like.save()
            return Response({"result": True})
        else:
            content.likes -= 1
            content.save()
            user.likes -= 1
            user.save()
            like = Like.objects.filter(owner=user)
            like.delete()
            return Response({"result": False})
