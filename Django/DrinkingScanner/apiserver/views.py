from django.shortcuts import render
from django.http import JsonResponse
from apiserver.views_help import *
from apiserver.views_pre import pre_time_weight
from django.views.decorators.csrf import csrf_exempt
from .models import AllData,UserInfo
import ast

# Test Page
def test(request):
    return render(request, 'apiserver/test.html')

# 앱 처음에 사용자 등록
@csrf_exempt
def register(request):
    if request.method == 'POST':
        # 요청 데이터
        user = request.POST['user']
        ability = request.POST['ability']

        print(user,ability)

        # 처리
        if UserInfo.objects.filter(user=user).count()==0:
            data = UserInfo()
            data.user = user
            data.sojuAbility = ability
            data.save()
            print('DB : ',UserInfo.objects.all().values()) 

        # 응답
        if UserInfo.objects.filter(user=user).count()==1:
            result = dict()
            result['status'] = 'success'
            return JsonResponse(result, status=200)
        else:
            result = dict()
            result['status'] = 'result error'
            return JsonResponse(result, status=203)

    else:
        result = dict()
        result['status'] = 'request error'
        return JsonResponse(result, status=203)

# 안드로이드에서 들어오는 원시데이터 -> CSV 파일로 저장
@csrf_exempt
def saveData(request):
    if request.method == 'POST':
        # 요청 데이터
        user = request.POST['user']
        date = request.POST['date']
        data = request.POST['data']

        data = ast.literal_eval(data)
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
    bestSpeed = float(request.GET['bestSpeed'])

    danger = False

    if existOriginCSV(user, date):
        # 처리
        df = up_information(user,date)
        writePreCSV(user,date,df)

        # DB 저장
        if AllData.objects.filter(user=user, date=date).exists() == False:
            savePreToDB(user,date,df,bestSpeed)

        # 속도 위험도 CHECK
        danger = checkDanger('speed_medium_check', bestSpeed, user)

        # 응답
        if existPreCSV(user, date) and (df.shape[0]==0 or AllData.objects.filter(user=user, date=date).exists() == True):
            result = dict()
            if danger == True:
                result['status'] = 'danger'
            else:
                result['status'] = 'no danger'
           
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

# 누적량, 속도 위험도 체크를 위한 동기화
@csrf_exempt
def syncData(request):
    if request.method == 'POST':
        # 요청 데이터
        user = request.POST['user']
        date = request.POST['date']
        beforeAmount = int(request.POST['beforeAmount'])
        bestSpeed = float(request.POST['bestSpeed'])

        print('beforeAmount:',beforeAmount)
        print('bestSpeed:',bestSpeed)

        danger = False

        if existOriginCSV(user, date):
            df = up_information(user, date)

            if (df.shape[0] != 0 and df.loc[df.shape[0] - 1, 'accumAmount'] != -999) or (df.shape[0]-1 >= 0):
                # 현재 누적량
                nowAmount = df.loc[df.shape[0] - 1, 'accumAmount']
                if nowAmount == -999: nowAmount = df.loc[df.shape[0] - 2, 'accumAmount']

                # 최고 속도 계산 (50초마다라서)
                print(nowAmount-beforeAmount)
                nowSpeed = float(nowAmount - beforeAmount / 50)
                if bestSpeed < nowSpeed: bestSpeed = nowSpeed

                print("nowAmount:",int(nowAmount))
                print("nowSpeed:",float(bestSpeed))

                # 누적량 위험도 CHECK
                danger = checkDanger('amount_medium_check',nowAmount,user)
                # danger = checkDanger('speed_medium_check', bestSpeed, user)
                danger = checkDanger('speed_medium_check', nowSpeed, user)

                # 응답
                result = dict()

                if danger == True:
                    result['status'] = 'danger'
                else:
                    result['status'] = 'no danger'

                result['beforeAmount'] = int(nowAmount)
                result['bestSpeed'] = float(bestSpeed)

                return JsonResponse(result, status=200)
            
            else:
                # 응답
                result = dict()
                result['status'] = 'success'
                result['beforeAmount'] = int(0)
                result['bestSpeed'] = float(bestSpeed)
                return JsonResponse(result, status=200)

    result = dict()
    result['status'] = 'request error'
    result['beforeAmount'] = int(-1)
    result['bestSpeed'] = float(-1)
    return JsonResponse(result, status=203)

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

@csrf_exempt
# 사용자가 설문 작성
def survey(request):
    if request.method == 'POST':
        # 요청 데이터
        user = request.POST['user']
        date = request.POST['date']
        print(user+date)
        print('count',AllData.objects.filter(user=user, date=date).count())
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
    
    result = dict()
    result['status'] = 'request error'
    return JsonResponse(result, status=203)

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