import pandas as pd
import numpy as np
import csv
import os
from apiserver.views_pre import pre, pre_time_weight
from .models import AllData

# 데이터 CSV 파일 저장 경로
path = 'C:/Users/codnj/OneDrive/바탕 화면/졸업과제/DrinkingScanner/Django/csv/'

# 원시 데이터 CSV 파일에 쓰기 (없으면 생성)
def writeOriginCSV(user,date,data):
    filepath = path + user + '_' + date + '.csv'
    file = open(filepath,'a',newline='')
    w = csv.writer(file)
    w.writerows(data)
    return file

# 가공 데이터 CSV 파일 생성
def writePreCSV(user,date,df):
    filepath = path + 'pre_' + user + '_' + date + '.csv'
    df.to_csv(filepath,mode="w")

# 원시 데이터 CSV 파일 읽기
def readOriginCSV(user,date):
    filepath = path + user + '_' + date + '.csv'
    df = pd.read_csv(filepath, names=['weight'])
    return df

# 가공 데이터 CSV 파일 읽기
def readPreCSV(user,date):
    filepath = path + 'pre_' + user + '_' + date + '.csv'
    df = pd.read_csv(filepath)
    return df

# 원시 데이터 CSV 파일 존재 여부 확인
def existOriginCSV(user,date):
    filepath = path + user + '_' + date + '.csv'
    return os.path.isfile(filepath)

# 가공 데이터 CSV 파일 존재 여부 확인
def existPreCSV(user,date):
    filepath = path + 'pre_' + user + '_' + date + '.csv'
    return os.path.isfile(filepath)

# 원시 데이터 가공
def up_information(user,date):
    # 전처리
    df_origin = readOriginCSV(user,date)
    data = pre(df_origin)

    up_index = [] # 잔을 든 횟수
    up_startTime = [] # 바닥에서 딱 잔 든 시간
    up_endTime = [] # 바닥에서 딱 잔 놓은 시간
    up_holdingTime = [] # 잔을 들고 있는 시간
    up_interval = [] # 놓은 후 몇 초만에 잔을 들었는가 (잔 간격)
    up_changeAmount = [] # 잔 들었을 때 변화량
    up_accumAmount = [] # 누적 마신 양
    up_drink = [] # 잔을 들고 마셨는지 부었는지

    index = 0 # 인덱스 증가시킬 변수
    before_endTime = 0 # 잔 간격 계산에 필요한 변수
    sum_weight = 0 # 누적 마신양 저장
    up_threshold = -1 # 술 잔을 들었다는 기준 값

    for i in range(0, len(data[0])):
        # 잔을 든 경우 + 잔 놓고 부은 경우
        if data[0][i] < up_threshold:
            # 잔을 든 경우
            if data[0][i] != -888:
                # 열에 따라 값 추가
                up_index.append(index)
                up_startTime.append(data[1][i])
                up_endTime.append(data[2][i])
                up_holdingTime.append(data[2][i] - data[1][i])
                up_interval.append(data[1][i] - before_endTime)
                # 잔 간격 (전 잔을 바닥에 놓은 시간 ~ 다음 잔을 바닥에서 든 시간)
                before_endTime = data[2][i]

                # 변화량, 누적양, 마신여부 추가
                if i == len(data[0])-1: # 마지막 (마신 여부를 알 수가 없음)
                    up_changeAmount.append(-999)
                    up_accumAmount.append(-999)
                    up_drink.append(None)

                else: # 마지막 아님 (마신 여부를 알 수가 있음)
                    up_changeAmount.append(data[0][i + 1] - data[0][i - 1])

                    if data[0][i+1] - data[0][i-1] < 0: # 마시면
                        sum_weight = sum_weight + (data[0][i-1] - data[0][i+1])
                        up_accumAmount.append(sum_weight)
                        up_drink.append(True)
                    else: # 안 마시면
                        up_accumAmount.append(sum_weight)
                        up_drink.append(False)

            # 잔을 놓고 부은 경우
            else:
                # 열에 따라 값 추가
                up_index.append(index)
                up_startTime.append(data[1][i])
                up_endTime.append(data[2][i])
                up_holdingTime.append(-888)
                up_interval.append(-888)
                up_accumAmount.append(sum_weight)
                up_drink.append(False)

                # 변화량, 누적양 추가
                if i == len(data[0]) - 1:
                    up_changeAmount.append(-999)
                else:
                    up_changeAmount.append(data[0][i+1] - data[0][i-1])

            # 반드시 해야 하는거
            index = index + 1

    # DataFrame
    data = {
        'index': up_index,
        'startTime': up_startTime,
        'endTime': up_endTime,
        'holdingTime': up_holdingTime,
        'interval': up_interval,
        'changeAmount': up_changeAmount,
        'accumAmount': up_accumAmount,
        'drink': up_drink
    }
    df = pd.DataFrame(data, index=np.arange(0,len(up_index)))

    # Test
    print('Up Information Result')
    print(data)
    print(df)

    return df

# 가공 데이터 DB에 저장
def savePreToDB(user,date,df):
    data = AllData()
    data.user = user
    data.date = date
    data.amount = df.loc[df.shape[0]-1,'accumAmount']
    if data.amount==-999: data.amount = df.loc[df.shape[0]-2,'accumAmount']
    data.time = 60
    data.maxSpeed = 30
    data.meanSpeed = 15
    data.save()
    print('Record Count : ', AllData.objects.filter(user=user, date=date).count())
    print('DB : ',AllData.objects.all().values())

# 설문 데이터 DB에 저장
def saveSurveyToDB(user, date, request):
    data = AllData.objects.get(user=user, date=date)
    # data = AllData.objects.filter(user=user, date=date).first()
    data.drunkenness = int(request.GET['drunkenness'])
    data.satisfaction = int(request.GET['satisfaction'])
    data.alcohol = request.GET['alcohol']
    data.who = request.GET['who']
    data.money = int(request.GET['money'])
    data.surveyCheck = True
    data.save()
    print('Record Count : ', AllData.objects.filter(user=user, date=date).count())
    print('DB : ',AllData.objects.all().values())