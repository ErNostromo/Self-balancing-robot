//https://github.com/jrowberg/i2cdevlib/blob/master/Arduino/MPU6050/MPU6050.cpp#L209

#include "MPU6050.h"
#include "I2Cdev.h"
#include "math.h"

#define status_led 13
#define step1 A0    //PORTC0
#define dir1 A1     //PORTC1
#define step2 A2    //PORTC2
#define dir2 A3     //PORTC3

MPU6050 mpu;
int acc_calibration_value = 0;                            //Enter the accelerometer calibration value

//Various settings
float pid_p_gain = 7;         //7                              //Gain setting for the P-controller (15)
float pid_i_gain = 0.1;       //0.1                               //Gain setting for the I-controller (1.5)
float pid_d_gain = 1;         //1                              //Gain setting for the D-controller (30)
float turning_speed = 30;                                    //Turning speed (20)
float max_target_speed = 200;                                //Max target speed (100)

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//Declaring global variables
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
byte start, received_byte, low_bat = 0;

int left_motor, throttle_left_motor, throttle_counter_left_motor, throttle_left_motor_memory;
int right_motor, throttle_right_motor, throttle_counter_right_motor, throttle_right_motor_memory;
int battery_voltage;
int receive_counter;
int gyro_pitch_data_raw, gyro_yaw_data_raw, accelerometer_data_raw;

long gyro_yaw_calibration_value, gyro_pitch_calibration_value;

unsigned long loop_timer;

float angle_gyro, angle_acc, angle, self_balance_pid_setpoint;
float pid_error_temp, pid_i_mem, pid_setpoint=-9, gyro_input, pid_output, pid_last_d_error;
float pid_output_left, pid_output_right;


void setup() {
  Serial.begin(9600);
  Wire.begin();
  pinMode (step1, OUTPUT);
  pinMode (dir1, OUTPUT);
  digitalWrite (step1, LOW);
  digitalWrite (dir1, LOW);
  pinMode (step2, OUTPUT);
  pinMode (dir2, OUTPUT);
  digitalWrite (step2, LOW);
  digitalWrite (dir2, LOW);
  pinMode(status_led, OUTPUT);
  digitalWrite(status_led, LOW);
  TWBR = 12;                                                                //Set the I2C clock speed to 400kHz
  //pinMode(2, OUTPUT);                                                       //Configure digital poort 2 as output
  //pinMode(3, OUTPUT);                                                       //Configure digital poort 3 as output

  cli();
    TCCR1A = 0;
  TCCR1B = 0;
  TCNT1 = 0;
  OCR1A = 319.0;
  TCCR1B |= (1 << WGM12);
  TCCR1B |= (0 << CS12) | (0 << CS11) | (1 << CS10);
  TIMSK1 |= (1 << OCIE1A);
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

  for (receive_counter = 0; receive_counter < 500; receive_counter++) {
    if (receive_counter % 15 == 0) digitalWrite(status_led, !digitalRead(status_led));
    gyro_yaw_calibration_value += mpu.getRotationX();
    gyro_pitch_calibration_value += mpu.getRotationY();
    gyro_yaw_calibration_value += Wire.read() << 8 | Wire.read();           //Combine the two bytes to make one integer
    gyro_pitch_calibration_value += Wire.read() << 8 | Wire.read();         //Combine the two bytes to make one integer

    delayMicroseconds(3700);
  }
  digitalWrite(status_led, HIGH);
  gyro_yaw_calibration_value /= 500;
  gyro_pitch_calibration_value /= 500;

  loop_timer = micros() + 4000;

}

void loop() {
  while (Serial.available() > 0) {
    pid_p_gain = Serial.parseFloat();
    pid_i_gain = Serial.parseFloat();
    pid_d_gain = Serial.parseFloat();

    if (Serial.read() == '\n') {
      Serial.print("Kp:");
      Serial.print(pid_p_gain);
      Serial.print("; Ki: ");
      Serial.print(pid_i_gain);
      Serial.print("; Kd:");
      Serial.println(pid_d_gain);
      delay(2500);
    }
  }
  accelerometer_data_raw = mpu.getAccelerationZ();
  accelerometer_data_raw += acc_calibration_value;
  if (accelerometer_data_raw > 8200)accelerometer_data_raw = 8200;          //Prevent division by zero by limiting the acc data to +/-8200;
  if (accelerometer_data_raw < -8200)accelerometer_data_raw = -8200;        //Prevent division by zero by limiting the acc data to +/-8200;
  angle_acc = asin((float)accelerometer_data_raw / 8200.0) * 57.296;

  if (start == 0 && angle_acc > -0.5 && angle_acc < 0.5) {                  //If the accelerometer angle is almost 0
    angle_gyro = angle_acc;
    start = 1;                                                              //Set the start variable to start the PID controller
    Serial.print (angle_gyro);
  }
  else {
    Serial.print (angle_acc);
  }
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

  pid_error_temp = angle_gyro - self_balance_pid_setpoint - pid_setpoint;
  if (pid_output > 10 || pid_output < -10)pid_error_temp += pid_output * 0.015 ;

  pid_i_mem += pid_i_gain * pid_error_temp;                                 //Calculate the I-controller value and add it to the pid_i_mem variable
  if (pid_i_mem > 400)pid_i_mem = 400;                                      //Limit the I-controller to the maximum controller output
  else if (pid_i_mem < -400)pid_i_mem = -400;
  pid_output = pid_p_gain * pid_error_temp + pid_i_mem + pid_d_gain * (pid_error_temp - pid_last_d_error);
  if (pid_output > 400)pid_output = 400;                                    //Limit the PI-controller to the maximum controller output
  else if (pid_output < -400)pid_output = -400;
  pid_last_d_error = pid_error_temp;                                        //Store the error for the next loop

  if (pid_output < 5 && pid_output > -5)pid_output = 0;                     //Create a dead-band to stop the motors when the robot is balanced

  if (angle_gyro > 30 || angle_gyro < -30 || start == 0 || low_bat == 1) {  //If the robot tips over or the start variable is zero or the battery is empty
    pid_output = 0;                                                         //Set the PID controller output to 0 so the motors stop moving
    pid_i_mem = 0;                                                          //Reset the I-controller memory
    start = 0;                                                              //Set the start variable to 0
    self_balance_pid_setpoint = 0;                                          //Reset the self_balance_pid_setpoint variable
  }

  pid_output_left = pid_output;                                             //Copy the controller output to the pid_output_left variable for the left motor
  pid_output_right = pid_output;                                            //Copy the controller output to the pid_output_right variable for the right motor
  //The self balancing point is adjusted when there is not forward or backwards movement from the transmitter. This way the robot will always find it's balancing point

  if (pid_setpoint == 0) {                                                  //If the setpoint is zero degrees
    if (pid_output < 0)self_balance_pid_setpoint += 0.0015;                 //Increase the self_balance_pid_setpoint if the robot is still moving forewards
    if (pid_output > 0)self_balance_pid_setpoint -= 0.0015;                 //Decrease the self_balance_pid_setpoint if the robot is still moving backwards
  }

  Serial.print(", ");
  Serial.print (pid_output);
  Serial.println(";");

  ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //Motor pulse calculations
  ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //To compensate for the non-linear behaviour of the stepper motors the folowing calculations are needed to get a linear speed behaviour.
  if (pid_output_left > 0)pid_output_left = 405 - (1 / (pid_output_left + 9)) * 5500;
  else if (pid_output_left < 0)pid_output_left = -405 - (1 / (pid_output_left - 9)) * 5500;

  if (pid_output_right > 0)pid_output_right = 405 - (1 / (pid_output_right + 9)) * 5500;
  else if (pid_output_right < 0)pid_output_right = -405 - (1 / (pid_output_right - 9)) * 5500;

  //Calculate the needed pulse time for the left and right stepper motor controllers
  if (pid_output_left > 0)left_motor = 400 - pid_output_left;
  else if (pid_output_left < 0)left_motor = -400 - pid_output_left;
  else left_motor = 0;

  if (pid_output_right > 0)right_motor = 400 - pid_output_right;
  else if (pid_output_right < 0)right_motor = -400 - pid_output_right;
  else right_motor = 0;

  //Copy the pulse time to the throttle variables so the interrupt subroutine can use them
  throttle_left_motor = left_motor;
  throttle_right_motor = right_motor;
  //Serial.print("Left_motor: ");
  //Serial.print (left_motor);
  //Serial.print("; right_motor: ");
  //Serial.println (right_motor);

  ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //Loop time timer
  ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //The angle calculations are tuned for a loop time of 4 milliseconds. To make sure every loop is exactly 4 milliseconds a wait loop
  //is created by setting the loop_timer variable to +4000 microseconds every loop.
  while (loop_timer > micros());
  loop_timer += 4000;
}

ISR (TIMER1_COMPA_vect) {
  //Left motor pulse calculations
  throttle_counter_left_motor ++;                                           //Increase the throttle_counter_left_motor variable by 1 every time this routine is executed
  if (throttle_counter_left_motor > throttle_left_motor_memory) {           //If the number of loops is larger then the throttle_left_motor_memory variable
    throttle_counter_left_motor = 0;                                        //Reset the throttle_counter_left_motor variable
    throttle_left_motor_memory = throttle_left_motor;                       //Load the next throttle_left_motor variable
    if (throttle_left_motor_memory < 0) {                                   //If the throttle_left_motor_memory is negative
      PORTC &= 0b11111101;                                                  //Set output 4 low to reverse the direction of the stepper controller
      throttle_left_motor_memory *= -1;                                     //Invert the throttle_left_motor_memory variable
    }
    else PORTC |= 0b00000010;                                               //Set output 4 high for a forward direction of the stepper motor
  }
  else if (throttle_counter_left_motor == 1) {
    PORTC |= 0b00000001;             //Set output 3 high to create a pulse for the stepper controller
    //Serial.println("pulse1");
  }
  else if (throttle_counter_left_motor == 2 || throttle_counter_left_motor == 0) {
    PORTC &= 0b11111110;             //Set output 3 low because the pulse only has to last for 20us
    //Serial.println("pulse0");
  }
  //right motor pulse calculations
  throttle_counter_right_motor ++;                                          //Increase the throttle_counter_right_motor variable by 1 every time the routine is executed
  if (throttle_counter_right_motor > throttle_right_motor_memory) {         //If the number of loops is larger then the throttle_right_motor_memory variable
    throttle_counter_right_motor = 0;                                       //Reset the throttle_counter_right_motor variable
    throttle_right_motor_memory = throttle_right_motor;                     //Load the next throttle_right_motor variable
    if (throttle_right_motor_memory < 0) {                                  //If the throttle_right_motor_memory is negative
      PORTC &= 0b11110111;                                                  //Set output 5 low to reverse the direction of the stepper controller
      throttle_right_motor_memory *= -1;                                    //Invert the throttle_right_motor_memory variable
    }
    else PORTC |= 0b00001000;                                               //Set output 5 high for a forward direction of the stepper motor
  }
  else if (throttle_counter_right_motor == 1) PORTC |= 0b00000100;                                                     //Set output 4 high to create a pulse for the stepper controller
  else if (throttle_counter_right_motor == 2 || throttle_counter_right_motor == 0)PORTC &= 0b11111011;           //Set output 4 low because the pulse only has to last for 20us


}

