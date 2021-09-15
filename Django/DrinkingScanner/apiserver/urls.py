from django.urls import path
from . import views

urlpatterns = [
    path('',views.test),
    path('saveData',views.saveData),
    path('preData',views.preData),
    path('toTimeWeight',views.toTimeWeight),
    path('toTimeAmount', views.toTimeAmount),
    path('survey', views.survey),
    path('clearDB',views.clearDB),
]