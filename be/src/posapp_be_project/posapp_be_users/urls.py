from django.urls import path
from django.urls import include
from django.conf.urls import url

from rest_framework.routers import DefaultRouter
from rest_framework.urlpatterns import format_suffix_patterns


from . import views

router = DefaultRouter()
router.register('profile', views.UserProfileViewSet, base_name='profile')
router.register('login', views.LoginViewSet, base_name='login')
#router.register('producta', views.)

#router.register('hello-viewset', views.HelloViewSet, base_name='hello-viewset')

urlpatterns = [
    #path('hello-view/', views.HelloApiView.as_view()),
    #path('', include(router.urls)),
    #path('profiles/', views.)
    #path('users/', views.ListUsers.as_view()),
    #path('<int:pk>/', views.ProfileDetail.as_view()),

    path('product/', views.list_products),
    url(r'^product/(?P<pk>[0-9]+)$', views.product_detail),
    url(r'', include(router.urls)),
]

#urlpatterns = format_suffix_patterns(urlpatterns)
