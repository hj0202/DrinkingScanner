from django.shortcuts import render
from django.http import JsonResponse
from apiserver.views_help import *
from apiserver.views_pre import pre_time_weight
from django.views.decorators.csrf import csrf_exempt
from .models import AllData

# Test Page
def test(request):
    return render(request, 'apiserver/test.html')

# 안드로이드에서 들어오는 원시데이터 -> CSV 파일로 저장
@csrf_exempt
def saveData(request):
    if request.method == 'POST':
        # 요청 데이터
        user = request.POST['user']
        date = request.POST['date']
        data = request.POST['data']

        data = makeTwoDimension(data)
       
        # 처리
        file = writeOriginCSV(user,date,data)
        file.close()

        # 응답
        if existOriginCSV(user,date):
            result = dict()
            result['status'] = 'success'
            # 실제 응답
            return JsonResponse(result, status=200)
            # 테스트 응답 (웹 페이지)
            #return render(request, 'apiserver/result.html',{'result': result})
        else:
            result = dict()
            result['status'] = 'result error'
            return JsonResponse(result, status=203)
            #return render(request, 'apiserver/result.html',{'result': result})
    else:
        result = dict()
        result['status'] = 'request error'
        return JsonResponse(result, status=203)
        #return render(request, 'apiserver/result.html',{'result': result})

# 원시데이터 가공한 데이터 -> CSV 파일로 저장
def preData(request):
    # 요청 데이터
    user = request.GET['user']
    date = request.GET['date']

    if existOriginCSV(user, date):
        # 처리
        df = up_information(user,date)
        writePreCSV(user,date,df)

        # DB 저장
        if AllData.objects.filter(user=user, date=date).exists() == False:
            savePreToDB(user,date,df)

        # 응답
        if existPreCSV(user, date) and (df.shape[0]==0 or AllData.objects.filter(user=user, date=date).exists() == True):
            result = dict()
            result['status'] = 'success'
            return JsonResponse(result, status=200)
            #return render(request, 'apiserver/result.html',{'result': result})
        else:
            result = dict()
            result['status'] = 'result error'
            return JsonResponse(result, status=203)
            # return render(request, 'apiserver/result.html', {'result': result})
    else:
        result = dict()
        result['status'] = 'request error'
        return JsonResponse(result, status=203)
        #return render(request, 'apiserver/result.html', {'result': result})

# 원시데이터 -> 시간(X) 무게(Y)
def toTimeWeight(request):
    # 요청 데이터
    user = request.GET['user']
    date = request.GET['date']

    if existPreCSV(user, date):
        # 처리
        df = readOriginCSV(user, date)
        data = pre_time_weight(df)
        x = data[0]
        y = data[1]

        # 응답
        if len(x)!=0 and len(x)==len(y):
            result = dict()
            result['status'] = 'success'
            result['x'] = x
            result['y'] = y
            return JsonResponse(result, status=200)
            #return render(request, 'apiserver/result.html', {'result': result})
        else:
            result = dict()
            result['status'] = 'result error'
            return JsonResponse(result, status=203)
            #return render(request, 'apiserver/result.html', {'result': result})
    else:
        result = dict()
        result['status'] = 'request error'
        return JsonResponse(result, status=203)
        #return render(request, 'apiserver/result.html', {'result': result})

# 가공데이터 -> 시간(X) 마신양(Y)
def toTimeAmount(request):
    # 요청 데이터
    user = request.GET['user']
    date = request.GET['date']

    if existPreCSV(user, date):
        # 처리
        df = readPreCSV(user, date)
        x = list(df['endTime'])
        y = list(df['accumAmount'])

        # 응답
        if len(x)!=0 and len(x)==len(y):
            result = dict()
            result['status'] = 'success'
            result['x'] = x
            result['y'] = y
            return JsonResponse(result, status=200)
            #return render(request, 'apiserver/result.html', {'result': result})
        else:
            result = dict()
            result['status'] = 'result error'
            return JsonResponse(result, status=203)
            #return render(request, 'apiserver/result.html', {'result': result})
    else:
        result = dict()
        result['status'] = 'request error'
        return JsonResponse(result, status=203)
        # return render(request, 'apiserver/result.html', {'result': result})


# 사용자가 설문 작성
def survey(request):
    # 요청 데이터
    user = request.GET['user']
    date = request.GET['date']

    if AllData.objects.filter(user=user, date=date).count() == 1:
        # DB 저장
        saveSurveyToDB(user,date,request)

        # 응답
        if AllData.objects.filter(user=user, date=date).first().surveyCheck == True:
            result = dict()
            result['status'] = 'success'
            return JsonResponse(result, status=200)
            # return render(request, 'apiserver/result.html', {'result': result})
        else:
            result = dict()
            result['status'] = 'result error'
            return JsonResponse(result, status=203)
            # return render(request, 'apiserver/result.html', {'result': result})
    else:
        result = dict()
        result['status'] = 'request error'
        return JsonResponse(result, status=203)
        # return render(request, 'apiserver/result.html', {'result': result})

def clearDB(request):
    alldata = AllData.objects.all()
    alldata.delete()

    print('DB : ',AllData.objects.all().values())

    if AllData.objects.count() == 0:
        result = dict()
        result['status'] = 'success'
        return JsonResponse(result, status=200)
        # return render(request, 'apiserver/result.html', {'result': result})
    else:
        result = dict()
        result['status'] = 'result error'
        return JsonResponse(result, status=203)
        # return render(request, 'apiserver/result.html', {'result': result})