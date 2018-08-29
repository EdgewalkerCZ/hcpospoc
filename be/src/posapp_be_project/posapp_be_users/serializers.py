from rest_framework import serializers

from .models import Product, UserProfile


class HelloSerializer(serializers.Serializer):
    """Name field serialize for testing"""

    name = serializers.CharField(max_length=10)

class UserProfileSerializer(serializers.ModelSerializer):
    class Meta:
        fields = (
            'mobile_number',
            'name',
        )
        model = UserProfile


#class ProductSerializer(serializers.ModelSerializer):
