#include <EEPROM.h>

float var1;

void setup() {
  // put your setup code here, to run once:
  Serial.begin(115200);
  while (!Serial);
  for (int i = 0 ; i < EEPROM.length() ; i++) {
    if (EEPROM.read(i)) {
      EEPROM.write(i, 0);
      Serial.println(i);
    }
  }
  EEPROM.put(0, 8.00);      //KP
  EEPROM.put(10, 0.75);     //KI
  EEPROM.put(20, 3.00);     //KD
  EEPROM.put(30, 5.00);     //PID_SETPOINT_DEFAULT
  EEPROM.put(40, 200.00);   //MAX_TARGET_SPEED
  EEPROM.put(50, 80.00);    //TURNING_SPEED
  
  EEPROM.get(0, var1);
  Serial.println(var1);
  EEPROM.get(10, var1);
  Serial.println(var1);
  EEPROM.get(20, var1);
  Serial.println(var1);
  EEPROM.get(30, var1);
  Serial.println(var1);
  EEPROM.get(40, var1);
  Serial.println(var1);
  EEPROM.get(50, var1);
  Serial.println(var1);
}

void loop() {
  // put your main code here, to run repeatedly:

}
