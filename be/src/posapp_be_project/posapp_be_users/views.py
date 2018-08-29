from django.shortcuts import render

from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework import status


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
