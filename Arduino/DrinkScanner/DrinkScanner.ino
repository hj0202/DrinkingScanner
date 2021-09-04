#include "HX711.h" // LoadCell Library
#include <SoftwareSerial.h> // Bluetooth Library

// 무게 교정용 상수
#define calibration_factor 1965000

// Pin Initialize
const int BLUETOOTH_RX_PIN = 2; 
const int BLUETOOTH_TX_PIN = 3;       
const int LOADCELL_DOUT_PIN = 4; 
const int LOADCELL_SCK_PIN  = 5; 
const int LED_RED_PIN   = 9;  
const int LED_GREEN_PIN = 10; 
const int LED_BLUE_PIN  = 11;     

// LoadCell Declaration
HX711 scale;

// Bluetooth Declaration
//(RX, TX)위치인데 교차연결해야하므로 TX, RX 순으로 넣음
SoftwareSerial bt(BLUETOOTH_TX_PIN,BLUETOOTH_RX_PIN); 

void setup() {
  Serial.begin(9600);
  bt.begin(9600);
  
  scale.begin(LOADCELL_DOUT_PIN, LOADCELL_SCK_PIN);
  pinMode(LED_RED_PIN, OUTPUT);
  pinMode(LED_GREEN_PIN, OUTPUT);
  pinMode(LED_BLUE_PIN, OUTPUT);
  
  setColor(255, 200, 100);
  
  // 0점 조절 
  scale.set_scale(); 
  scale.tare();    
  scale.set_scale(calibration_factor);
}

void loop() {
  // 무게 값을 받아옴
  int data = scale.get_units(10) * 1000;
  String data_str = String("010");
  
  // 블루투스로 무게 값을 보냄
  for (int i = 0; i < data_str.length(); i++)
  {
    bt.write(data_str[i]);
  }
  bt.write('\n');
}

void setColor(int red, int green, int blue)
{
  analogWrite(LED_RED_PIN, red);
  analogWrite(LED_GREEN_PIN, green);
  analogWrite(LED_BLUE_PIN, blue); 
}
