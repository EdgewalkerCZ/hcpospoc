from django.db import models
from django.core.validators import MaxValueValidator, MinValueValidator


import datetime


# Create your models here.


#product type

#product category
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






#Sub category

#Brand name

#Product name

#Price

#include GST or not

#Quantity

#Product description
