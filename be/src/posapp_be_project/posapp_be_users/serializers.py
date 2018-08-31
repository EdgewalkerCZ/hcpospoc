from rest_framework import serializers

from .models import Product, UserProfile


#class HelloSerializer(serializers.Serializer):
#    """Name field serialize for testing"""
#
#    name = serializers.CharField(max_length=10)
#
class UserProfileSerializer(serializers.ModelSerializer):
    class Meta:
        fields = (
            'mobile_number',
            'name',
        )
        model = UserProfile




class ProductSerializer(serializers.ModelSerializer):
    class Meta:
        model = Product
        fields = '__all__'

#class UserSerializer(serializers.ModelSerializer):
#    password = serializers.CharField(write_only=True)
#
#    class Meta:
#        model = UserProfile
#        fields = ('first_name', 'last_name', 'mobile', 'password')
#
#    def create(self, validated_data):
#        user = super(UserSerializer, self).create(validated_data)
#        user.set_password(validated_data['password'])
#        user.save()
#        return user
