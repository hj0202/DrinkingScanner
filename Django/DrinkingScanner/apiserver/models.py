from django.db import models
from django.core.validators import MinValueValidator,MaxValueValidator

# Create your models here.
class AllData(models.Model):
    # type
    # AlcoholType = (
    #     ('L','Liquor'),
    #     ('B','Beer'),
    #     ('S','Soju'),
    #     ('W','Wine'),
    #     ('M','Makgeolli'),
    # )
    # WhoType = (
    #     ('FR', 'Friend'),
    #     ('FA', 'Family'),
    #     ('BU', 'Business'),
    #     ('AL', 'Alone'),
    #     ('SO', 'Someone'),
    # )

    # column
    user = models.CharField(max_length=10)
    date = models.CharField(max_length=4)

    amount = models.IntegerField()
    time = models.IntegerField()
    maxSpeed = models.IntegerField()
    meanSpeed = models.IntegerField()

    drunkenness = models.IntegerField(null=True, blank=True, validators = [MinValueValidator(1),MaxValueValidator(5)])
    satisfaction = models.IntegerField(null=True, blank=True, validators = [MinValueValidator(1),MaxValueValidator(5)])

    # alcohol = models.CharField(blank=True, choices=AlcoholType, max_length=10)
    alcohol = models.CharField(blank=True, max_length=10)
    money = models.IntegerField(null=True, blank=True)
    # who = models.CharField(blank=True, choices=WhoType, max_length=10)
    who = models.CharField(blank=True, max_length=10)

    surveyCheck = models.BooleanField(default=False)

class UserInfo(models.Model):
    user = models.CharField(max_length=10)
    sojuAbility = models.IntegerField()