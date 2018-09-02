from rest_framework import serializers

from .models import Product
from .models import UserProfile



#class HelloSerializer(serializers.Serializer):
#    """Name field serialize for testing"""
#
#    name = serializers.CharField(max_length=10)
#
class UserProfileSerializer(serializers.ModelSerializer):
    class Meta:
        fields = (
            'id',
            'mobile_number',
            'name',
            'password',
        )
        model = UserProfile
        extra_kwargs = {'password': {'write_only': True}}

    def create(self, validated_data):
        """Create and return a new user"""

        user = UserProfile(
            mobile_number=validated_data['mobile_number'],
            name=validated_data['name'],
        )

        user.set_password(validated_data['password'])

        user.save()

        return user




class ProductSerializer(serializers.ModelSerializer):
    #id = serializers.IntegerField(read_only=True)



    class Meta:
        model = Product
        fields = '__all__'
        extra_kwargs = {'warehouse': {'read_only': True}}

    warehouse = serializers.ReadOnlyField(source='warehouse.id')


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
