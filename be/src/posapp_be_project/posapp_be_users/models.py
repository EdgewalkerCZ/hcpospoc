from django.db import models
from django.contrib.auth.models import AbstractBaseUser
from django.contrib.auth.models import PermissionsMixin
from django.contrib.auth.models import BaseUserManager


from django.core.validators import RegexValidator
from django.core.validators import MaxValueValidator, MinValueValidator

from django_currentuser.middleware import (
    get_current_user, get_current_authenticated_user)


# Create your models here.

class UserProfileManager(BaseUserManager):
    """Helps Django to work with custom user model"""

    def create_user(self, mobile_number, name, password=None):

        if not mobile_number:
            raise ValueError('Users must have a mobile number adress.')

        #email = self.normalize_email(email)
        user = self.model(mobile_number=mobile_number, name=name)

        user.set_password(password)
        user.save(using=self._db)

        return user

    def create_superuser(self, mobile_number, name, password):
        """create and saves a new superuser"""

        user = self.create_user(mobile_number,name,password)

        user.is_superuser = True

        user.is_staff = True

        user.save(using=self._db)

        return user

    def create_POS(self, mobile_number, name, password=None):


        user = self.create_user(mobile_number=mobile_number, name=name)


        user.save(using=self._db)

        return user

class UserProfile(AbstractBaseUser, PermissionsMixin):
    """Represent profile, including POC now"""

    email = models.EmailField(max_length=255, unique=True)
    phone_regex = RegexValidator(regex=r'^\+?1?\d{9,15}$', message="Phone number must be entered in the format: '+999999999'. Up to 15 digits allowed.")
    mobile_number = models.CharField(validators=[phone_regex], max_length=17, blank=False, unique=True) # validators should be a list


    name = models.CharField(max_length=255)
    isActive = models.BooleanField(default=True)

    is_staff = models.BooleanField(default=False)

    is_POS = models.BooleanField(default=True)

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



class Product(models.Model):

    name = models.CharField(max_length=45)
    brand_name = models.CharField(max_length=45)

    CATEGORY_CHOICES = (
        ('AUTOMOTIVE', 'Automotive'),
        ('BEAUTY', 'Beauty'),
        ('BOOKS', 'Books'),
        ('CELLPHONES', 'Cell Phones'),
        ('CONSUMER', 'Consumer'),
        ('GROCERY', 'Grocery'),
        ('SCIENTIFIC', 'Scientific'),
        ('INDUSTRIAL', 'Industrial'),
    )

    category = models.CharField(max_length=2, choices=CATEGORY_CHOICES, default='CONSUMER')

    subcategory = models.CharField(max_length=45, blank=True)

    BRAND_CHOICES = (
        ('PANASONIC', 'Panasonic'),
        ('SAMSUNG', 'Samsung'),
        ('ATUL', 'Atul'),
        ('NESTLE', 'Nestle'),
        ('ASTRALPIPES', 'Astral Pipes'),
    )

    brand = models.CharField(max_length=2, choices=BRAND_CHOICES, default='', blank=True)

    quantity = models.SmallIntegerField() #fix min/max value

    description = models.TextField(max_length=1000)

    price = models.IntegerField(default=0, validators=[MaxValueValidator(999999),MinValueValidator(0)])

    isGst = models.BooleanField()

    from django_currentuser.db.models import CurrentUserField

    warehouse = CurrentUserField()

    def __str__(self):
        return self.title
