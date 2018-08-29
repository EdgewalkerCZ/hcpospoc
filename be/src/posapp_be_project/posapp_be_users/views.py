from django.shortcuts import render

from rest_framework import viewsets
from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework import generics
from rest_framework import status

from .models import UserProfile
from .serializers import UserProfileSerializer
from . import serializers

# Create your views here.

class HelloApiView(APIView):
    """Test API view"""

    serializer_class = serializers.HelloSerializer


    def get(self, request, format=None):
        """Return list of APIView features"""

        an_apiview = [
            'Uses HTTP Methods as a function (get, post, patch, put, delete)',
            'Similar to traditional django',
            'isMappedManuallytoURL',
        ]

        return Response({'message': 'Hello', 'an_apiview': an_apiview})

    def post(self,request):
        """Create hello message with name"""

        serializer = serializers.HelloSerializer(data=request.data)

        if serializer.is_valid():
            name = serializer.data.get('name')
            message = 'Hello {0}'.format(name)
            return Response({'message': message})
        else:
            return Response(
                serializer.errors, status=status.HTTP_400_BAD_REQUEST)


    def put(self, request, pk=None):
        """Updating"""

        return Response({'method': 'put'})

    def patch(self, request, pk=None):
        """Patch request, only updates fields required"""

        return Response({'method': 'patch'})


    def delete(self, request, pk=None):
        """Patch request, only updates fields required"""

        return Response({'method': 'delete'})

class HelloViewSet(viewsets.ViewSet):
    """Test API Viewset"""

    serializer_class = serializers.HelloSerializer

    def list(self, request):
        """Returns hello message"""



        a_viewset = [
            'Uses actions (list, create, retrieve, update, partial_update)',
            'Automatically maps to URLs using Routers',
            'Provides more functionality with less code'
        ]

        return Response({'message': 'Hello!', 'a_viewset': a_viewset})


    def create(self, request):
        """Create hello message"""


        serializer = serializers.HelloSerializer(data=request.data)

        if serializer.is_valid():
            name = serializer.data.get('name')
            message = 'Hello {0}'.format(name)
            return Response({'message': message})
        else:
            return Response(serializer.error, status=status.HTTP_400_BAD_REQUEST)

    def create(self, request, pk=None):
        """getting object by ID"""

        return Response({'http_method':'GET'})


class ListUsers(generics.ListCreateAPIView):
    queryset = UserProfile.objects.all()
    serializer_class = UserProfileSerializer


class ProfileDetail(generics.RetrieveUpdateDestroyAPIView):
    queryset = UserProfile.objects.all()
    serializer_class = UserProfileSerializer
