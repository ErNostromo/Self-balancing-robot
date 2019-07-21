#include "Wire.h"
#include "I2Cdev.h"
#include "MPU6050.h"
#include "math.h"

MPU6050 mpu;

int16_t accY, accZ, accX, gyroX, gyroY, gyroZ;
float accAngle;
float RaccAngle;
String inputString = "";
char inChar = '\0';
int offset = 0;

// newAngle = angle measured with atan2 using the accelerometer
// newRate = angle measured using the gyro
// looptime = loop time in millis()

float Complementary2(float newAngle, float newRate, int looptime) {
  float k = 10;
  float dtc2 = float(looptime) / 1000.0;
  float x_angle2C=0;

  float x1 = (newAngle -   x_angle2C) * k * k;
  float y1 = dtc2 * x1 + y1;
  float x2 = y1 + (newAngle -   x_angle2C) * 2 * k + newRate;
  x_angle2C = dtc2 * x2 + x_angle2C;

  return x_angle2C;
}

void setup() {
  mpu.initialize();
  Serial.begin(2000000);
}

void loop() {
  accX = mpu.getAccelerationX();
  accZ = mpu.getAccelerationZ();
  accY = mpu.getAccelerationY();
  gyroX = mpu.getRotationX();
  gyroY = mpu.getRotationY();
  gyroZ = mpu.getRotationZ();

  accAngle = (atan2(accX, accZ) * RAD_TO_DEG);
  RaccAngle = (atan2(accX, accZ) * RAD_TO_DEG) - offset;

  if (isnan(accAngle));
  else {
    Serial.println(RaccAngle);
  }
  if (Serial.available() > 0) {
    while (Serial.available()) {
      Serial.read();
    }
    offset = accAngle;
  }

}
