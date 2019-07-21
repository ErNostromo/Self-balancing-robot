#include <Arduino.h>
#line 1 "/home/sam_s3p10l/Scaricati/Arduino/SELF-BALANCING ROBOT/firmware/unstable/unstable.ino"
#line 1 "/home/sam_s3p10l/Scaricati/Arduino/SELF-BALANCING ROBOT/firmware/unstable/unstable.ino"
//TODO: fix pid_output_[left/right] going 0.5
//TODO: add previous value check in processing app
//TODO: fix turning_speed not working
//TODO: fix instant speed cut when falling ("add negative acceleration")
//TODO: create pc interface

//https://github.com/jrowberg/i2cdevlib/blob/master/Arduino/MPU6050/MPU6050.cpp#L209

#include "MPU6050.h"
#include "I2Cdev.h"
#include "math.h"
#include <EEPROM.h>
#include <Servo.h>

//ARDUINO PINS
const int status_led = A1;
const int step1 = 15;
const int dir1 = 14;
const int step2 = 16;
const int dir2 = 10;
const int servo = 5;
const int trigger = 7;
const int echo = 8;
const int enable_motor1_pin = A2;

//USEFUL CONSTANTS (COUNTER)
const int counter_default = 15;
const int loop_time = 4000;

//EEPROM
const int kp_addr = 0;
const int ki_addr = 10;
const int kd_addr = 20;
const int pid_setpoint_default_addr = 30;
const int max_target_speed_addr = 200;
const int turning_speed_addr = 40;
const float kp_def = 8;
const float ki_def = 0.75;
const float kd_def = 3;
const float pid_setpoint_default = 5.5;
const float max_target_speed_def = 200;
const float turning_speed_def = 80;

boolean enable_motor1 = HIGH;

MPU6050 mpu;
Servo myServo;
boolean first_time_low_bat = false;
char ultras_enable_char = 0;
boolean ultras_enable = 0;
int acc_calibration_value = 0; //Enter the accelerometer calibration value
int counter = counter_default;

//Various settings
float pid_p_gain;       //8                              //Gain setting for the P-controller (15)
float pid_i_gain;       //0.7                               //Gain setting for the I-controller (1.5)
float pid_d_gain;       //3                              //Gain setting for the D-controller (30)
float turning_speed;    //80                              //Turning speed (20)
float max_target_speed; //200                             //Max target speed (100)

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//Declaring global variables
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
byte start, low_bat = 0;

int left_motor, throttle_left_motor, throttle_counter_left_motor, throttle_left_motor_memory;
int right_motor, throttle_right_motor, throttle_counter_right_motor, throttle_right_motor_memory;
int battery_voltage;
int receive_counter;
int gyro_pitch_data_raw, gyro_yaw_data_raw, accelerometer_data_raw;

long gyro_yaw_calibration_value, gyro_pitch_calibration_value;

unsigned long loop_timer;

float angle_gyro, angle_acc, angle, self_balance_pid_setpoint;

float pid_error_temp, pid_i_mem, pid_setpoint = pid_setpoint_default, gyro_input, pid_output, pid_last_d_error;
float pid_output_left, pid_output_right;
float pid_vel_left = 0, pid_vel_right = 0;
float pid_setpoint_bt = pid_setpoint_default;
float pid_setpoint_offset = 0; //PER TERRENI DIFFICILI

char inChar = 0, type = 0, inChar1 = 0, inChar2 = 0;
String inputString = "";
bool stringComplete = false;
int firstIndex, secondIndex, thirdIndex;

int servo_pos = 90;

#define DEBUG
//#define WAIT_SERIAL

#ifdef DEBUG
unsigned long prev_time = 0;
unsigned long sum = 0;
unsigned int avg = 0;
#endif
/*
  #ifdef DEBUG                                      //Macros are usually in all capital letters.
  #define DPRINT(...) Serial.print(__VA_ARGS__)     //DPRINT is a macro, debug print
  #define DPRINTLN(...) Serial.println(__VA_ARGS__) //DPRINTLN is a macro, debug print with new line
  #define BPRINT(...) Serial1.print(__VA_ARGS__)
  #define BPRINTLN(...) Serial1.print(__VA_ARGS__)
  #else
  #define DPRINT(...)   //now defines a blank line
  #define DPRINTLN(...) //now defines a blank line
  #define BPRINT(...)   //now defines a blank line
  #define BPRINTLN(...) //now defines a blank line
  #endif
*/
#line 112 "/home/sam_s3p10l/Scaricati/Arduino/SELF-BALANCING ROBOT/firmware/unstable/unstable.ino"
void setup();
#line 215 "/home/sam_s3p10l/Scaricati/Arduino/SELF-BALANCING ROBOT/firmware/unstable/unstable.ino"
void loop();
#line 364 "/home/sam_s3p10l/Scaricati/Arduino/SELF-BALANCING ROBOT/firmware/unstable/unstable.ino"
void update_keys();
#line 441 "/home/sam_s3p10l/Scaricati/Arduino/SELF-BALANCING ROBOT/firmware/unstable/unstable.ino"
void update_gyro();
#line 465 "/home/sam_s3p10l/Scaricati/Arduino/SELF-BALANCING ROBOT/firmware/unstable/unstable.ino"
void fix_pid();
#line 490 "/home/sam_s3p10l/Scaricati/Arduino/SELF-BALANCING ROBOT/firmware/unstable/unstable.ino"
void update_direction();
#line 628 "/home/sam_s3p10l/Scaricati/Arduino/SELF-BALANCING ROBOT/firmware/unstable/unstable.ino"
void normalize_pid();
#line 112 "/home/sam_s3p10l/Scaricati/Arduino/SELF-BALANCING ROBOT/firmware/unstable/unstable.ino"
void setup()
{
  Serial1.begin(115200);
  Serial.begin(115200);
  Wire.begin();
  pinMode(step1, OUTPUT);
  pinMode(dir1, OUTPUT);
  digitalWrite(dir1, LOW);
  pinMode(step2, OUTPUT);
  pinMode(dir2, OUTPUT);
  digitalWrite(dir2, LOW);
  pinMode(status_led, OUTPUT);
  pinMode(trigger, OUTPUT);
  pinMode(echo, INPUT);
  myServo.attach(5);
  digitalWrite(step1, LOW);
  digitalWrite(step2, LOW);
  delay(20);
  digitalWrite(step1, HIGH);
  digitalWrite(step2, HIGH);
  delay(20);
  digitalWrite(step1, LOW);
  digitalWrite(step2, LOW);

  //digitalWrite(enable_motor1_pin, enable_motor1);

  //delay(5000);

  TWBR = 12; //Set the I2C clock speed to 400kHz
  cli();
  TCCR3A = 0;
  TCCR3B = 0;
  TCNT3 = 0;
  OCR3A = 319;
  TCCR3B |= (1 << WGM32);
  TCCR3B |= (0 << CS32) | (0 << CS31) | (1 << CS30);
  TIMSK3 |= (1 << OCIE3A);
  sei();
  mpu.initialize();
  mpu.setFullScaleGyroRange(0);  //0 = +/- 250 degrees/sec
  mpu.setFullScaleAccelRange(1); //1 = +/- 4g
  /*
   *  *          |   ACCELEROMETER    |           GYROSCOPE
    DLPF_CFG | Bandwidth | Delay  | Bandwidth | Delay  | Sample Rate
    ---------+-----------+--------+-----------+--------+-------------
    0        | 260Hz     | 0ms    | 256Hz     | 0.98ms | 8kHz
    1        | 184Hz     | 2.0ms  | 188Hz     | 1.9ms  | 1kHz
    2        | 94Hz      | 3.0ms  | 98Hz      | 2.8ms  | 1kHz
    3        | 44Hz      | 4.9ms  | 42Hz      | 4.8ms  | 1kHz <---
    4        | 21Hz      | 8.5ms  | 20Hz      | 8.3ms  | 1kHz
    5        | 10Hz      | 13.8ms | 10Hz      | 13.4ms | 1kHz
    6        | 5Hz       | 19.0ms | 5Hz       | 18.6ms | 1kHz
    7        |   -- Reserved --   |   -- Reserved --   | Reserved
  */
  mpu.setDLPFMode(3);

#ifdef WAIT_SERIAL
  while (!Serial)
  {
  }
#endif

  EEPROM.get(0, pid_p_gain);
  EEPROM.get(10, pid_i_gain);
  EEPROM.get(20, pid_d_gain);
  EEPROM.get(30, pid_setpoint_default);
  EEPROM.get(40, max_target_speed);
  EEPROM.get(50, turning_speed);
#ifdef DEBUG
  Serial.println(pid_p_gain);
  Serial.println(pid_i_gain);
  Serial.println(pid_d_gain);
  Serial.println(pid_setpoint_default);
  Serial.println(max_target_speed);
  Serial1.println(turning_speed);
  Serial1.println(pid_p_gain);
  Serial1.println(pid_i_gain);
  Serial1.println(pid_d_gain);
  Serial1.println(pid_setpoint_default);
  Serial1.println(max_target_speed);
  Serial1.println(turning_speed);
#endif

  for (receive_counter = 0; receive_counter < 500; receive_counter++)
  {
    if (receive_counter % 15 == 0)
      digitalWrite(status_led, !digitalRead(status_led));
    gyro_yaw_calibration_value += mpu.getRotationX();
    gyro_pitch_calibration_value += mpu.getRotationY();
    //gyro_yaw_calibration_value += Wire.read() << 8 | Wire.read();           //Combine the two bytes to make one integer
    //gyro_pitch_calibration_value += Wire.read() << 8 | Wire.read();         //Combine the two bytes to make one integer

    delayMicroseconds(3700);
  }
  digitalWrite(status_led, HIGH);
  gyro_yaw_calibration_value /= 500;
  gyro_pitch_calibration_value /= 500;
  loop_timer = micros() + loop_time;

  //pinMode(2, OUTPUT);                                                       //Configure digital poort 2 as output
  //pinMode(3, OUTPUT);                                                       //Configure digital poort 3 as output
}

void loop()
{
#ifdef DEBUG
  prev_time = micros();
#endif
  if (start == 0 && angle_acc > -2 && angle_acc < 5)
  { //If the accelerometer angle is almost 0
    angle_gyro = angle_acc;
    start = 1; //Set the start variable to start the PID controller
    enable_motor1 = LOW;
#ifdef DEBUG
    //Serial1.print("att");
#endif
    //Serial.print("SI");
    //Serial.println (angle_gyro);
  }
  else
  {
    //Serial.println (angle_acc);
  }

  if (angle_gyro > 30 || angle_gyro < -30 || start == 0 || low_bat == 1)
  { //If the robot tips over or the start variable is zero or the battery is empty
#ifdef DEBUG
    //Serial1.print("natt");
#endif
    pid_output = 0;
    pid_i_mem = 0;                 //Reset the I-controller memory
    start = 0;                     //Set the start variable to 0
    self_balance_pid_setpoint = 0; //Reset the self_balance_pid_setpoint variable
    enable_motor1 = HIGH;
  }

  if (low_bat)
  {
    digitalWrite(status_led, LOW);
    delay(250);
    digitalWrite(status_led, HIGH);
    delay(250);
  }

  if (ultras_enable)
  {
    //USARE A VOSTRO RISCHIO E PERICOLO
    digitalWrite(trigger, LOW);
    digitalWrite(trigger, HIGH);
    delayMicroseconds(10);
    digitalWrite(trigger, LOW);
    long durata = pulseIn(echo, HIGH);
    long distanza = 0.034 * durata / 2;
    //Serial.println(distanza);
    if (distanza > 12)
      pid_setpoint_bt = -3.5 + pid_setpoint_default;
    else if (distanza < 8)
      pid_setpoint_bt = 3.5 + pid_setpoint_default;
    else
      pid_setpoint_bt = pid_setpoint_default;
    if (pid_setpoint > pid_setpoint_bt)
      pid_setpoint -= 0.05;
    if (pid_setpoint < pid_setpoint_bt)
      pid_setpoint += 0.05;
    if (pid_output > max_target_speed * -1)
      pid_setpoint -= 0.01;
    if (pid_output < max_target_speed)
      pid_setpoint += 0.01;
  }

  update_keys();

  update_gyro();

  update_direction();

  //Serial.println(pid_setpoint);
  /*
    Serial.print("inChar1: ");
    Serial.print(inChar1);
    Serial.print("; ");
    Serial.print(pid_setpoint);
  */

  //digitalWrite(enable_motor1_pin, enable_motor1);

  /*
    //Serial.println(pid_setpoint);
    Serial.print("; ");
    Serial.print (pid_output);
    Serial.println(";");
  */

  fix_pid();
  ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //Loop time timer
  ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //The angle calculations are tuned for a loop time of 4 milliseconds. To make sure every loop is exactly 4 milliseconds a wait loop
  //is created by setting the loop_timer variable to +4000 microseconds every loop.
  //Serial.println(micros() - time_now);
  if (counter <= 0)
  {
    counter = counter_default;
    Serial1.print(angle_acc);
    Serial1.print(" ");
    Serial1.print(angle_gyro);
    Serial1.print(" ");
    Serial1.print(pid_error_temp);
    Serial1.print(" ");
    Serial1.print(pid_setpoint);
    Serial1.print(" ");
    Serial1.print(pid_output);
    Serial1.print(" ");
    Serial1.print(pid_vel_left);
    Serial1.print(" ");
    Serial1.print(pid_vel_right);
#ifdef DEBUG
    avg = sum / counter_default;
    Serial.print(angle_acc);
    Serial.print(" ");
    Serial.print(angle_gyro);
    Serial.print(" ");
    Serial.print(pid_error_temp);
    Serial.print(" ");
    Serial.print(pid_setpoint);
    Serial.print(" ");
    Serial.print(pid_output);
    Serial.print(" ");
    Serial.print(pid_vel_left);
    Serial.print(" ");
    Serial.print(pid_vel_right);
    Serial.print(" ");
    Serial.print(avg);
    Serial.print(";");
    Serial1.print(" ");
    Serial1.print(avg);
    sum = 0;
    avg = 0;
#endif
    Serial1.println(" ;");
  }
  counter--;
#ifdef DEBUG
  sum += (micros() - prev_time);
  prev_time = micros();
#endif
  while (loop_timer > micros())
  {
  }
  loop_timer += 4000;
}

void update_keys()
{
  if (Serial1.available() > 0)
  {
    while (Serial1.available() > 0 && inChar != ';')
    {
      inChar = Serial1.read();
      if (inChar != ';')
      {
        inputString += inChar;
        stringComplete = false;
      }
      else
        stringComplete = true;
    }
    if (stringComplete)
    {
      inputString += ';';
      type = inputString.charAt(0);
      switch (type)
      {
      case 'v':
        inChar1 = inputString.charAt(1);
        inChar2 = inputString.charAt(2);
        break;
      case 's':
        EEPROM.put(pid_setpoint_default_addr, inputString.substring(1, inputString.indexOf(';')).toFloat());
        inChar1 = 0;
        break;
      case 'k':
        firstIndex = inputString.indexOf(',');
        pid_p_gain = inputString.substring(1, firstIndex).toFloat();

        secondIndex = inputString.indexOf(',', firstIndex + 1);
        pid_i_gain = inputString.substring(firstIndex + 1, secondIndex).toFloat();

        thirdIndex = inputString.indexOf(';');
        pid_d_gain = inputString.substring(secondIndex + 1, thirdIndex).toFloat();
        EEPROM.put(kp_addr, pid_p_gain);
        EEPROM.put(ki_addr, pid_i_gain);
        EEPROM.put(kd_addr, pid_d_gain);
        break;

      case 't':
        turning_speed = inputString.substring(1).toFloat();
        EEPROM.put(turning_speed_addr, turning_speed);
        break;

      case 'c':
        servo_pos = inputString.substring(1).toFloat();
        break;

      case 'u':
        ultras_enable_char = inputString.charAt(1);
        if (ultras_enable_char == '1')
          ultras_enable = 1;
        else if (ultras_enable_char == '0')
          ultras_enable = 0;
      }
      inputString = "";
      inChar = 0;
      stringComplete = false;
      receive_counter = 0;
    }
  }

  if (receive_counter <= 25)
    receive_counter++;
  else
  {
    inChar1 = 0;
    inChar2 = 0;
    ultras_enable = 0;
    servo_pos = 90;
  }
}

void update_gyro()
{
  myServo.write(servo_pos);
  digitalWrite(status_led, !ultras_enable);
  accelerometer_data_raw = mpu.getAccelerationZ();
  accelerometer_data_raw += acc_calibration_value;
  if (accelerometer_data_raw > 8200)
    accelerometer_data_raw = 8200; //Prevent division by zero by limiting the acc data to +/-8200;
  if (accelerometer_data_raw < -8200)
    accelerometer_data_raw = -8200; //Prevent division by zero by limiting the acc data to +/-8200;
  angle_acc = asin((float)accelerometer_data_raw / 8200.0) * 57.296;

  //Load the accelerometer angle in the angle_gyro variable
  gyro_yaw_data_raw = mpu.getRotationX();
  gyro_pitch_data_raw = mpu.getRotationY();
  gyro_pitch_data_raw -= gyro_pitch_calibration_value;
  angle_gyro += gyro_pitch_data_raw * 0.000031;
  //angle_gyro = gyro_pitch_data_raw;
  gyro_yaw_data_raw -= gyro_yaw_calibration_value;
  //SE IL GIROSCOPIO NON È PERFETTAMENTE INSTALLATO:
  //angle_gyro -= gyro_yaw_data_raw * 0.0000003;
  angle_gyro = angle_gyro * 0.9996 + angle_acc * 0.0004;
}

void fix_pid()
{
  pid_error_temp = angle_gyro - self_balance_pid_setpoint - pid_setpoint - pid_setpoint_offset;
  if (pid_output > 10 || pid_output < -10)
    pid_error_temp += pid_output * 0.015;

  pid_i_mem += pid_i_gain * pid_error_temp; //Calculate the I-controller value and add it to the pid_i_mem variable
  if (pid_i_mem > 400)
    pid_i_mem = 400; //Limit the I-controller to the maximum controller output
  else if (pid_i_mem < -400)
    pid_i_mem = -400;
  pid_output = pid_p_gain * pid_error_temp + pid_i_mem + pid_d_gain * (pid_error_temp - pid_last_d_error);
  if (pid_output > 400)
    pid_output = 400; //Limit the PI-controller to the maximum controller output
  else if (pid_output < -400)
    pid_output = -400;
  pid_last_d_error = pid_error_temp; //Store the error for the next loop

  if (pid_output < 5 && pid_output > -5)
    pid_output = 0; //Create a dead-band to stop the motors when the robot is balanced

  pid_output_left = pid_output + pid_vel_left;   //Copy the controller output to the pid_output_left variable for the left motor
  pid_output_right = pid_output + pid_vel_right; //Copy the controller output to the pid_output_right variable for the right motor
}

void update_direction()
{
  if (!ultras_enable)
  {
    if ((inChar1 == '0' || inChar1 == '1' || inChar1 == '2' || inChar1 == '3'))
    {
      if (inChar1 == '0')
        pid_setpoint_bt = 3.5 + pid_setpoint_default;
      else if (inChar1 == '1')
        pid_setpoint_bt = 2.63 + pid_setpoint_default;
      else if (inChar1 == '2')
        pid_setpoint_bt = 1.75 + pid_setpoint_default;
      else if (inChar1 == '3')
        pid_setpoint_bt = 0.875 + pid_setpoint_default;
      if (pid_setpoint < pid_setpoint_bt - 0.05)
        pid_setpoint += 0.05;
      if (pid_setpoint > pid_setpoint_bt + 0.05)
        pid_setpoint -= 0.05;
      /*
        if (pid_output < max_target_speed && pid_setpoint >= pid_setpoint_bt-0.05 && pid_setpoint <= pid_setpoint_bt + 0.05) {
        pid_setpoint_offset += 0.01;
        }
      */
    }
    else if ((inChar1 == '5' || inChar1 == '6' || inChar1 == '7' || inChar1 == '8'))
    {
      if (inChar1 == '5')
        pid_setpoint_bt = -0.875 + pid_setpoint_default;
      else if (inChar1 == '6')
        pid_setpoint_bt = -1.75 + pid_setpoint_default;
      else if (inChar1 == '7')
        pid_setpoint_bt = -2.63 + pid_setpoint_default;
      else if (inChar1 == '8')
        pid_setpoint_bt = -3.5 + pid_setpoint_default;
      if (pid_setpoint > pid_setpoint_bt + 0.05)
        pid_setpoint -= 0.05;
      if (pid_setpoint < pid_setpoint_bt - 0.05)
        pid_setpoint += 0.05;
      /*
        if (pid_output > max_target_speed * -1 && pid_setpoint >= pid_setpoint_bt-0.05 && pid_setpoint <= pid_setpoint_bt + 0.05) {
        pid_setpoint_offset -= 0.01;
        }
      */
    }

    else
    {
      if (pid_setpoint > 0.5 + pid_setpoint_default)
        pid_setpoint -= 0.05; //If the PID setpoint is larger then 0.5 reduce the setpoint with 0.05 every loop
      else if (pid_setpoint < -0.5 + pid_setpoint_default)
        pid_setpoint += 0.05; //If the PID setpoint is smaller then -0.5 increase the setpoint with 0.05 every loop
      else
        pid_setpoint = pid_setpoint_default; //If the PID setpoint is smaller then 0.5 or larger then -0.5 set the setpoint to 0
      if (pid_setpoint == pid_setpoint_default)
      { //If the setpoint is zero degrees
        if (pid_output < 0)
          self_balance_pid_setpoint += 0.0015; //Increase the self_balance_pid_setpoint if the robot is still moving forewards
        if (pid_output > 0)
          self_balance_pid_setpoint -= 0.0015; //Decrease the self_balance_pid_setpoint if the robot is still moving backwards
      }
    }

    switch (inChar2)
    {
    case '8':
      if (pid_output_right < pid_output + turning_speed)
        pid_vel_right += 0.1;
      if (pid_output_left > pid_output - turning_speed)
        pid_vel_left -= 0.1;
      break;

    case '7':
      if (pid_output_right < pid_output + (3 * turning_speed / 4))
        pid_vel_right += 0.1;
      if (pid_output_left > pid_output - (3 * turning_speed / 4))
        pid_vel_left -= 0.1;
      break;

    case '6':
      if (pid_output_right < pid_output + (2 * turning_speed / 4))
        pid_vel_right += 0.1;
      if (pid_output_left > pid_output - (2 * turning_speed / 4))
        pid_vel_left -= 0.1;
      break;

    case '5':
      if (pid_output_right < pid_output + (turning_speed / 4))
        pid_vel_right += 0.1;
      if (pid_output_left > pid_output - (turning_speed / 4))
        pid_vel_left -= 0.1;
      break;

    case '3':
      if (pid_output_right > pid_output - (turning_speed / 4))
        pid_vel_right -= 0.1;
      if (pid_output_left < pid_output + (turning_speed / 4))
        pid_vel_left += 0.1;
      break;

    case '2':
      if (pid_output_right > pid_output - (turning_speed / 2))
        pid_vel_right -= 0.1;
      if (pid_output_left < pid_output + (turning_speed / 2))
        pid_vel_left += 0.1;
      break;
    case '1':
      if (pid_output_right > pid_output - (3 * turning_speed / 4))
        pid_vel_right -= 0.1;
      if (pid_output_left < pid_output + (3 * turning_speed / 4))
        pid_vel_left += 0.1;
      break;

    case '0':
      if (pid_output_right > pid_output - turning_speed)
        pid_vel_right -= 0.1;
      if (pid_output_left < pid_output + turning_speed)
        pid_vel_left += 0.1;
      break;
    default:

      if (pid_output_right > 0.3 + pid_output)
        pid_vel_right -= 0.5;
      else if (pid_output_right < -0.3 - pid_output)
        pid_vel_right += 0.5;
      else
        pid_vel_right = 0;
      if (pid_output_left > 0.3 + pid_output)
        pid_vel_left -= 0.5;
      else if (pid_output_left < -0.3 - pid_output)
        pid_vel_left += 0.5;
      else
        pid_vel_left = 0;

      //pid_vel_right = pid_vel_left = 0;
    }
  }
}

void normalize_pid()
{
  ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //Motor pulse calculations
  ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //To compensate for the non-linear behaviour of the stepper motors the folowing calculations are needed to get a linear speed behaviour.
  if (pid_output_left > 0)
    pid_output_left = 405 - (1 / (pid_output_left + 9)) * 5500;
  else if (pid_output_left < 0)
    pid_output_left = -405 - (1 / (pid_output_left - 9)) * 5500;

  if (pid_output_right > 0)
    pid_output_right = 405 - (1 / (pid_output_right + 9)) * 5500;
  else if (pid_output_right < 0)
    pid_output_right = -405 - (1 / (pid_output_right - 9)) * 5500;

  //Calculate the needed pulse time for the left and right stepper motor controllers
  if (pid_output_left > 0)
    left_motor = 400 - pid_output_left;
  else if (pid_output_left < 0)
    left_motor = -400 - pid_output_left;
  else
    left_motor = 0;

  if (pid_output_right > 0)
    right_motor = 400 - pid_output_right;
  else if (pid_output_right < 0)
    right_motor = -400 - pid_output_right;
  else
    right_motor = 0;

  //Copy the pulse time to the throttle variables so the interrupt subroutine can use them
  throttle_left_motor = left_motor;
  throttle_right_motor = right_motor;
}

ISR(TIMER3_COMPA_vect)
{
  //Left motor pulse calculations
  throttle_counter_left_motor++; //Increase the throttle_counter_left_motor variable by 1 every time this routine is executed
  if (throttle_counter_left_motor > throttle_left_motor_memory)
  {                                                   //If the number of loops is larger then the throttle_left_motor_memory variable
    throttle_counter_left_motor = 0;                  //Reset the throttle_counter_left_motor variable
    throttle_left_motor_memory = throttle_left_motor; //Load the next throttle_left_motor variable
    if (throttle_left_motor_memory < 0)
    {                                   //If the throttle_left_motor_memory is negative
      PORTB &= 0b11110111;              //Set output 4 low to reverse the direction of the stepper controller
      throttle_left_motor_memory *= -1; //Invert the throttle_left_motor_memory variable
    }
    else
      PORTB |= 0b00001000; //Set output 4 high for a forward direction of the stepper motor
  }
  else if (throttle_counter_left_motor == 1)
  {
    PORTB |= 0b00000010; //Set output 3 high to create a pulse for the stepper controller
    //Serial1.println("pulse1");
  }
  else if (throttle_counter_left_motor == 2 || throttle_counter_left_motor == 0)
  {
    PORTB &= 0b11111101; //Set output 3 low because the pulse only has to last for 20us
    //Serial1.println("pulse0");
  }
  //right motor pulse calculations
  throttle_counter_right_motor++; //Increase the throttle_counter_right_motor variable by 1 every time the routine is executed
  if (throttle_counter_right_motor > throttle_right_motor_memory)
  {                                                     //If the number of loops is larger then the throttle_right_motor_memory variable
    throttle_counter_right_motor = 0;                   //Reset the throttle_counter_right_motor variable
    throttle_right_motor_memory = throttle_right_motor; //Load the next throttle_right_motor variable
    if (throttle_right_motor_memory < 0)
    {                                    //If the throttle_right_motor_memory is negative
      PORTB &= 0b10111111;               //Set output 5 low to reverse the direction of the stepper controller
      throttle_right_motor_memory *= -1; //Invert the throttle_right_motor_memory variable
    }
    else
      PORTB |= 0b01000000; //Set output 5 high for a forward direction of the stepper motor
  }
  else if (throttle_counter_right_motor == 1)
    PORTB |= 0b00000100; //Set output 4 high to create a pulse for the stepper controller
  else if (throttle_counter_right_motor == 2 || throttle_counter_right_motor == 0)
    PORTB &= 0b11111011; //Set output 4 low because the pulse only has to last for 20us
}

