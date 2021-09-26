from django.urls import path
from . import views

urlpatterns = [
    path('',views.test),
    path('register',views.register),
    path('saveData',views.saveData),
    path('preData',views.preData),
    path('syncData',views.syncData),
    path('toTimeWeight',views.toTimeWeight),
    path('toTimeAmount', views.toTimeAmount),
    path('survey', views.survey),
    path('clearDB',views.clearDB),
]