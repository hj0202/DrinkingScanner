import statistics

def pre_time_weight(df_origin):
    time = list(df_origin.index)
    weight = list(df_origin['weight'])
    return [time,weight]

def pre_downpour(data):
    time = data[0]
    weight = data[1]

    weight_result = list(weight)
    pour_threshold = 3 # 술잔을 놓고 부었다고 기준 짓는 값

    # 술잔을 놓고 부었다고 생각하는 구간 -888 값으로 변경
    for i in range(1, len(weight) - 1):
        if weight[i] - weight[i - 1] > pour_threshold and weight[i + 1] - weight[i] > pour_threshold:
            if weight[i - 1] >= 0:
                weight_result[i] = -888  # POUR

    # Test
    print('DownPour Result')
    print(time)
    print(weight_result)
    # Test

    return [time,weight_result]

def pre_change_frequency(data):
    number = [] # 수
    frequency = [] # 빈도수
    startTime = [] # 구간 시작 시간
    endTime = [] # 구간 끝 시간
    same = 0 # 연속으로 같은 개수

    for i in range(1, len(data[1])):
        # 같으면
        if data[1][i - 1] == data[1][i]:
            same = same + 1
            # 같고 마지막이면
            if i == (len(data[1]) - 1):
                number.append(data[1][i - 1])
                frequency.append(same + 1)
                startTime.append(data[0][i - same])
                endTime.append(data[0][i])
                same = 0
        # 다르면
        else:
            number.append(data[1][i - 1])
            frequency.append(same + 1)
            startTime.append(data[0][i - same - 1])
            endTime.append(data[0][i - 1])
            same = 0
            # 다르고 마지막이면
            if i == (len(data[1]) - 1):
                number.append(data[1][i])
                frequency.append(1)
                startTime.append(data[0][i])
                endTime.append(data[0][i])

    # Test
    print('Change Frequency Result')
    print(number)
    print(frequency)
    print(startTime)
    print(endTime)
    # Test
    return [number,frequency,startTime,endTime]

def pre_combine_frequency(number,frequency):
    result_num = [] # 결과 number 리스트
    result_fre = [] # 결과 frequency 리스트
    fre = dict() # 연속되지 않은 같은 수의 frequency를 합치기 위한 dictionary

    for i in range(len(number)):
        fre[number[i]] = 0
    for i in range(len(number)):
        fre[number[i]] += frequency[i]
    for n, f in fre.items():
        result_num.append(n)
        result_fre.append(f)

    return result_num,result_fre

def pre_mode(number,frequency):
    mode_nums = [] # 가장 큰 freqeuncy를 가진 수들
    mode_fre = max(frequency) # 가장 큰 freqeuncy

    for i in range(0, len(number)):
        if mode_fre == frequency[i]:
            mode_nums.append(number[i])

    if len(mode_nums) == 0:
        mode_idx = frequency.index(mode_fre)
        return number[mode_idx]
    else:
        return statistics.mean(mode_nums)

def pre_find_bestweight(data):
  number = []
  startTime = []
  endTime = []
  up_threshold = -1

  # 잔이 놓여 있는 구간
  down_number = []
  down_frequency = []

  # 잔을 들고 있는 구간
  up_number = []
  up_frequency = []

  for i in range(len(data[0])):

      # 술잔 DOWN
      if data[0][i] >= up_threshold:

          # 하나의 술잔 DOWN 구간에 값 추가
          down_number.append(data[0][i])
          down_frequency.append(data[1][i])

          # 하나의 술잔 DOWN 구간을 -111 또는 -888로 저장
          if len(up_number)!=0:
              startTime.append(data[2][i - len(up_number)])
              endTime.append(data[3][i - 1])

              if data[0][i-1] != -888: number.append(-111)
              else: number.append(-888)

              up_number = []
              up_frequency = []

      # 술잔 UP
      else:

          # 하나의 술잔 UP 구간에 값 추가
          up_number.append(data[0][i])
          up_frequency.append(data[1][i])

          # 술잔 UP 한 구간을 최고의 무게 값으로 저장
          if len(down_number)!=0:
              startTime.append(data[2][i-len(down_number)])
              endTime.append(data[3][i-1])

              down_number, down_frequency = pre_combine_frequency(down_number,down_frequency)
              best_weight = pre_mode(down_number,down_frequency)
              number.append(best_weight)

              down_number = []
              down_frequency = []

      # 마지막 값 처리
      if i == (len(data[0])-1):
        if data[0][i] >= up_threshold and len(down_number) > 1: # 37 37 37 37
              startTime.append(data[2][i-len(down_number)+1])
              endTime.append(data[3][i])
              down_number, down_frequency = pre_combine_frequency(down_number,down_frequency)
              best_weight = pre_mode(down_number,down_frequency)
              number.append(best_weight)
        if data[0][i] >= up_threshold and len(down_number) == 1: # -32 -32 -32 37
              startTime.append(data[2][i])
              endTime.append(data[3][i])
              number.append(data[0][i])
        if data[0][i] < up_threshold and len(up_number) > 1: # -32 -32 -32 -32
              startTime.append(data[2][i-len(up_number)+1])
              endTime.append(data[3][i])
              if data[0][i] != -888: number.append(-111)
              else: number.append(-888)
        if data[0][i] < up_threshold and len(up_number) == 1: # -37 37 37 -32
              startTime.append(data[2][i])
              endTime.append(data[3][i])
              if data[0][i] != -888: number.append(-111)
              else: number.append(-888)

  # Test
  print('Best Weight Result')
  print(number)
  print(startTime)
  print(endTime)
  # Test

  return [number,startTime,endTime]

def pre(df_origin):
    data = pre_time_weight(df_origin)
    data = pre_downpour(data)
    data = pre_change_frequency(data)
    data = pre_find_bestweight(data)
    return data