from rest_framework import serializers


class HelloSerializer(serializers.Serializer):
    """Name field serialize for testing"""

    name = serializers.CharField(max_length=10)
