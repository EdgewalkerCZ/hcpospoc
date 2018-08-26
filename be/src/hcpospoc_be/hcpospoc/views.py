from django.shortcuts import render
from rest_framework.views import APIView
from rest_framework.response import Response

# Create your views here.

class HelloApiView(APIView):
    """Test API view"""

    def get(self, request, format=None):
        """Return list of APIView features"""

        an_apiview = [
            'Uses HTTP Methods as a function (get, post, patch, put, delete)',
            'Similar to traditional django',
            'isMappedManuallytoURL',
        ]

        return Response({'message': 'Hello', 'an_apiview': an_apiview})
