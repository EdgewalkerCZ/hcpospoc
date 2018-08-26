from django.db import models
from django.contrib.auth.models import AbstractBaseUser
from django.contrib.auth.models import PermissionsMixin
from django.contrib.auth.models import BaseUserManager

# Create your models here.

class UserProfileManager(BaseUserManager):
    """Helps Django to work with custom user model"""

    def create_user(self, email, name, password=None):

        if not email:
            raise ValueError('Users must have an email adress.')

        email = self.normalize_email(email)
        user = self.model(email=email, name=name)

        user.set_password(password)
        user.save(using=self._db)

        return user

    def create_superuser(self, email, name, password):
        """create and saves a new superuser"""

        user = self.create_user(email,name,password)

        user.is_superuser = True

        user.is_staff = True

        user.save(using=self._db)

        return user

class pos_profile(AbstractBaseUser, PermissionsMixin):
    """Represent POS profile"""

    email = models.EmailField(max_length=255, unique=True)
    name = models.CharField(max_length=255)
    isActive = models.BooleanField(default=True)

    is_staff = models.BooleanField(default=False)

    objects = UserProfileManager()

    USERNAME_FIELD = 'email'
    REQUIRED_FIELDS = ['name']

    def get_full_name(self):
        """Get Full Usernamen"""

        return self.name

    def get_short_name(self):
        """Used to get users short name"""

        return self.name

    def __str__(self):
        """Django Uses this when it needs to convert object to string"""

        return self.email
