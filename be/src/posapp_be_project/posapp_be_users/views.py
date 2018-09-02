from django.shortcuts import render

from rest_framework import viewsets
from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework import generics
from rest_framework import status

from django.http import HttpResponse

from rest_framework.decorators import api_view

from rest_framework.views import APIView

from rest_framework.authentication import TokenAuthentication
from rest_framework.authtoken.serializers import AuthTokenSerializer
from rest_framework.authtoken.views import ObtainAuthToken
from rest_framework.permissions import IsAuthenticatedOrReadOnly

from .models import UserProfile
from .models import Product
from .serializers import UserProfileSerializer
from .serializers import ProductSerializer
from . import serializers

from . import permissions

from rest_framework import filters

# Create your views here.

class UserProfileViewSet(viewsets.ModelViewSet):
    """Handles creating and updating profiles"""

    serializer_class = UserProfileSerializer
    queryset = UserProfile.objects.all()

    authentication_classes = (TokenAuthentication,)
    permission_classes = (permissions.UpdateOwnProfile,)

    filter_backends = (filters.SearchFilter,)
    search_fields = ('name', 'mobile_number',)


class LoginViewSet(viewsets.ViewSet):
    """Checks login and password and returns an auth Token"""

    serializer_class = AuthTokenSerializer

    def create(self, request):
        """Use ObtainAuthToken APIView to validate and create a token"""

        return ObtainAuthToken().post(request)


class ProductViewSet(viewsets.ModelViewSet):
    """Class implementing second approach to products"""

    authentication_classes = (TokenAuthentication,)
    permission_classes = (permissions.PostOwnStatus,)
    serializer_class = ProductSerializer
    #queryset = Product.objects.all()
    filter_backends = (filters.SearchFilter,)
    search_fields = ('name', 'brand_name', 'category')

    def get_queryset(self):
        user = self.request.user
        return Product.objects.filter(warehouse=user)

    def perform_create(self, serializer):
        """sets the user profile to the logged in user"""
        serializer.save(warehouse=self.request.user)




#@api_view(['GET'])
#def list_products(request, format=None):
#    products = Product.objects.all()
#    serializer = ProductSerializer(products, many=True)
#    return Response(serializer.data)


#@api_view(['GET', 'POST', 'PUT', 'DELETE']) #add and in future delete or update products

#def product_detail(request, pk, format=None):

#    try:
#        product = Product.objects.get(pk=pk)
#    except Product.DoesNotExist:
#        return Response(status=status.HTTP_404_NOT_FOUND)
#
#    if request.method == 'GET':
#        serializer = ProductSerializer(product)
#        return Response(serializer.data)
#
#    elif request.method == 'POST':
#        serializer = ProductSerializer(data=request.data)
#        if serializer.is_valid():
#            serializer.save()
#            return Response(serializer.data, status=status.HTTP_201_CREATED)
#        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)









#class HelloApiView(APIView):
#    """Test API view"""
#
#    serializer_class = serializers.HelloSerializer
#
#
#    def get(self, request, format=None):
#        """Return list of APIView features"""
#
#        an_apiview = [
#            'Uses HTTP Methods as a function (get, post, patch, put, delete)',
#            'Similar to traditional django',
#            'isMappedManuallytoURL',
#        ]
#
#        return Response({'message': 'Hello', 'an_apiview': an_apiview})
#
#    def post(self,request):
#        """Create hello message with name"""
#
#
#        if serializer.is_valid():
#            name = serializer.data.get('name')
#            message = 'Hello {0}'.format(name)
#            return Response({'message': message})
#        else:
#            return Response(
#                serializer.errors, status=status.HTTP_400_BAD_REQUEST)
#
#
#    def put(self, request, pk=None):
#        """Updating"""
#
#        return Response({'method': 'put'})
#
#    def patch(self, request, pk=None):
#        """Patch request, only updates fields required"""
#
#        return Response({'method': 'patch'})
#
#
#    def delete(self, request, pk=None):
#        """Patch request, only updates fields required"""
#
#        return Response({'method': 'delete'})
#
#class HelloViewSet(viewsets.ViewSet):
#    """Test API Viewset"""
#
#    serializer_class = serializers.HelloSerializer
#    def list(self, request):
#        """Returns hello message"""
#
#        a_viewset = [
#            'Uses actions (list, create, retrieve, update, partial_update)',
#            'Automatically maps to URLs using Routers',
#            'Provides more functionality with less code'
#        ]
#
#        return Response({'message': 'Hello!', 'a_viewset': a_viewset})
#
#
#    def create(self, request):
#        """Create hello message"""
#
#
#        serializer = serializers.HelloSerializer(data=request.data)
#
#        if serializer.is_valid():
#            name = serializer.data.get('name')
#            message = 'Hello {0}'.format(name)
#            return Response({'message': message})
#        else:
#            return Response(serializer.error, status=status.HTTP_400_BAD_REQUEST)
#
#    def create(self, request, pk=None):
#        """getting object by ID"""
#
#        return Response({'http_method':'GET'})
#
#
#class ListUsers(generics.ListCreateAPIView):
#    queryset = UserProfile.objects.all()
#    serializer_class = UserProfileSerializer
#
#
#class ProfileDetail(generics.RetrieveUpdateDestroyAPIView):
#    queryset = UserProfile.objects.all()
#    serializer_class = UserProfileSerializer


#class CreateProfile(['POST'])
#@permission_classes((AllowAny,))
#def create_user(request):
#    serialized = UserSerializer(data=request.data)
#    if serialized.is_valid():
#        serialized.save()
#        return Response(serialized.data, status=status.HTTP_201_CREATED)
#    else:
#        return Response(serialized._errors, #status=status.HTTP_400_BAD_REQUEST)
