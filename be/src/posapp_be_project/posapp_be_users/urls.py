from django.urls import path
from django.urls import include

from rest_framework.routers import DefaultRouter


from . import views

#router = DefaultRouter()
#router.register('hello-viewset', views.HelloViewSet, base_name='hello-viewset')

urlpatterns = [
    #path('hello-view/', views.HelloApiView.as_view()),
    #path('', include(router.urls)),
    #path('profiles/', views.)
    #path('users/', views.ListUsers.as_view()),
    #path('<int:pk>/', views.ProfileDetail.as_view()),

    path('listproducts', views.list_products),
]
