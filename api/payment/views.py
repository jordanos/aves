from django.http import request
from rest_framework import generics, permissions, serializers, status
from rest_framework.views import APIView
from rest_framework.response import Response
from .serializers import CreatePurchaseSerializer, CheckPurchaseSerializer, InfoPurchaseSerializer
from helpers.utils import Util
from authentication.models import User
from rest_framework.parsers import MultiPartParser, FormParser
from .models import Purchase
from content.models import Content


class CreatePurchaseApiView(generics.CreateAPIView):
    '''
    Api endpoint to create payment
    '''
    serializer_class = CreatePurchaseSerializer
    # permission_classes = (permissions.IsAuthenticated,)

    def perform_create(self, serializer):
        serializer.is_valid(raise_exception=True)
        return serializer.save(id=Util.create_uuid())


class InfoPurchaseApiView(generics.RetrieveAPIView):
    '''
    Api endpoint to create payment
    '''
    serializer_class = InfoPurchaseSerializer
    queryset = Content.objects.all()
    lookup_field = 'id'

    def get_queryset(self):
        return self.queryset.filter()


class CheckPurchaseApiView(generics.GenericAPIView):
    '''
    Api endpoint to check user payment
    '''
    serializer_class = CheckPurchaseSerializer
    # permission_classes = (permissions.IsAuthenticated,)

    def post(self, request):
        """
        check if content bought and return result: True, key and nonce, or result: False
        """
        serializer = self.serializer_class(data=request.data)
        serializer.is_valid()
        validated_data = serializer.data
        count = Purchase.objects.filter(
            owner=validated_data["owner"], content=validated_data["content"]).count()
        if count > 0:
            content = Content.objects.get(pk=validated_data["content"])
            return Response({
                'result': True,
                'key': content.key,
                'nonce': content.nonce
            }, status.HTTP_200_OK)

        return Response({
            'result': False
        }, status.HTTP_200_OK)
