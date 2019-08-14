import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import android.app.Activity;

import ketai.net.bluetooth.*;
import ketai.ui.*;
import ketai.net.*;
import oscP5.*;

float distanza;
int ultras_enable = 0;
color cTrue = color(0, 255, 0), cFalse = color(255, 0, 0), cDisabled = color(73, 24, 0);
float X, Y;
String speedX, speedY;
boolean connect_bt= false, disconnect_bt = false, send = false;
String mac = "00:21:13:03:D0:7A", text;
float kp, ki, kd, pid_setpoint_default, turning_speed, pos;
Activity act;
Button button_connect, button_disconnect, button_send;
Joystick joy;
KetaiBluetooth bt;
Led led_connected;
Slider slider_kp, slider_ki, slider_kd, slider_pid_setpoint, slider_turning_speed, slider_servo;
checkBox check;
int stato_conn=0, stato_disconn=0, stato_send = 0;
boolean device_found = false, tab1 = true, tab2 = false;
ArrayList<String> devices;

void setup() {
  fullScreen();
  orientation(PORTRAIT);
  button_connect = new Button(10, 150, (width/3-20), 100, "CONNECT");
  button_disconnect = new Button(width/3+10, 150, (width/3-20), 100, "DISCONNECT");
  button_send = new Button(width/2-(width/2-20)/2, 7*height/8, (width/2-20), 100, "SEND");
  joy = new Joystick (width/2, height-400, 700, 100);
  act = this.getActivity();
  led_connected = new Led (width-150-20, 150+50, 150, "", cTrue, cFalse);
  float interf_y_min = height/4-50;
  float interf_y_max = 3*height/4+100;
  float interf_h = interf_y_max - interf_y_min;
  slider_kp = new Slider (width/2-100, interf_y_min, width/2+200, 150, 0, 20, 8, "Kp");
  slider_ki = new Slider (width/2-100, (interf_y_min + interf_h*1/4), width/2+200, 150, 0, 20, 0.75, "Ki");
  slider_kd = new Slider (width/2-100, (interf_y_min + interf_h*2/4), width/2+200, 150, 0, 20, 3, "Kd");
  slider_pid_setpoint = new Slider (width/2-100, (interf_y_min + interf_h*3/4), width/2+200, 150, -15, 15, 5, "Pid_setpoint");
  slider_turning_speed = new Slider (width/2-100, interf_y_max, width/2+200, 150, 100, 255, 200, "Turning speed");
  slider_servo = new Slider (width/2-100, interf_y_min, width/2+200, 150, 0, 180, 90, "Camera");
  check = new checkBox (width/2, height/2);
  bt.start();
}

void draw() {
  background(200, 210, 205);
  rectMode(CORNER);
  fill(200, 210, 205);
  rect(0, 0, 400, 100);
  rect(400, 0, 400, 100);
  rect(0, 100, width, height-100);
  fill(0);
  textAlign(CENTER, CENTER);
  textSize(50);
  text("Remote", 200, 50);
  text("Constants", 600, 50);
  if (mousePressed && mouseX > 0 && mouseX < 400 && mouseY > 0 && mouseY < 100) {
    tab1 = true;
    tab2 = false;
  }
  if (mousePressed && mouseX > 400 && mouseX < 800 && mouseY > 0 && mouseY < 100) {
    tab1 = false; 
    tab2 = true;
  }
  devices = bt.getConnectedDeviceNames();
  if (devices.size()>0) {
    for (String device : devices) {
      if (device.equals("arduino_bluetooth")) {
        device_found = true;
        break;
      } else {
        device_found = false;
      }
    }
  } else device_found = false;

  button_connect.buttonDisplay();
  button_disconnect.buttonDisplay();


  switch (stato_conn) {
    case (0):
    if (connect_bt) {
      stato_conn = 1; 
      println("stato_conn 1");
      connect_bt = false;
    }
    break;

    case (1):
    if (device_found) {
      showToast("Device already connected.");
      stato_conn = 0;
      break;
    }
    stato_conn = 2;
    println("stato_conn 2");
    showToast("Connecting...");
    bt.connectDevice(mac);
    break;

    case (2):
    if (device_found) {
      stato_conn = 0;
      stato_disconn = 0;
      showToast("Connected!");
      println("stato conn 0");
      break;
    }
  }
  switch (stato_disconn) {
    case(0):
    if (disconnect_bt) {
      stato_disconn = 1; 
      println("stato_disc 1");
      disconnect_bt = false;
    }
    break;

    case (1):
    if (device_found) {
      stato_disconn = 2;
      println("stato_disc 2");
      bt.disconnectDevice(mac);
      break;
    } else {
      showToast("Device not connected");
      stato_disconn = 0;
      break;
    }
    case (2):
    if (!device_found) {
      stato_disconn = 0;
      stato_conn = 0;
      println("stato_disc 0");
      showToast("Disconnected");
    }
  }
  led_connected.Display(device_found);
  if (tab1) {
    joy.Display();
    slider_servo.display();
    check.display();
    textAlign(RIGHT, CENTER);
    text("Modalita' ultrasuoni: ", width/2-50, height/2+50);
    if (device_found && stato_disconn==0) {
      X = joy.getX();
      Y = joy.getY();
      if (-350 <= X && X <= -280) speedX = "0";
      if (-279 < X && X <= -210) speedX = "1";
      if (-209 < X && X <= -140) speedX = "2";
      if (-139 < X && X <= -70) speedX = "3";
      if (-69< X && X <=69) speedX = "4";
      if (70 < X && X <= 139) speedX = "5";
      if (140 < X && X <= 209) speedX = "6";
      if (210 < X && X <= 279) speedX = "7";
      if (280 < X && X <= 350) speedX = "8";
      if (-350 <= Y && Y <= -280) speedY = "0";
      if (-279 < Y && Y <= -210) speedY = "1";
      if (-209 < Y && Y <= -140) speedY = "2";
      if (-139 < Y && Y <= -70) speedY = "3";
      if (-69< Y && Y <=69) speedY = "4";
      if (70 < Y && Y <= 139) speedY = "5";
      if (140 < Y && Y <= 209) speedY = "6";
      if (210 < Y && Y <= 279) speedY = "7";
      if (280 < Y && Y <= 350) speedY = "8";
      bt.write(mac, ("v"+speedY+speedX+";").getBytes());
      pos = slider_servo.getValue();
      text = "c" + pos + ";";
      bt.write(mac, text.getBytes());
      if (check.enabled()) ultras_enable = 1;
      else ultras_enable = 0;
      text = "u" + ultras_enable + ";";
      bt.write(mac, text.getBytes());
    }
  } else if (tab2) {
    button_send.buttonDisplay();
    strokeWeight(2);
    slider_kp.display();
    slider_ki.display();
    slider_kd.display();
    slider_pid_setpoint.display();
    slider_turning_speed.display();

    kp = slider_kp.getValue();
    ki = slider_ki.getValue();
    kd = slider_kd.getValue();
    pid_setpoint_default = slider_pid_setpoint.getValue();
    turning_speed = slider_turning_speed.getValue();

    switch (stato_send) {
    case 0:
      if (send) stato_send = 1;
      break;

    case 1:
      if (device_found) stato_send = 2;
      else if (!device_found) {
        showToast("Device not connected");
        stato_send = 0;
      }
      break;
    case 2:
      text = "k" + kp +"," + ki + "," + kd + ";";
      bt.write(mac, text.getBytes());
      text = "s" + pid_setpoint_default + ";";
      bt.write(mac, text.getBytes());
      text = "t" + turning_speed + ";";
      bt.write(mac, text.getBytes());
      text = "c" + pos + ";";
      bt.write(mac, text.getBytes());
      showToast("TEXT SENT");
      stato_send = 0;
      send = false;
      break;
    }
  }
}

void mouseReleased() {
  if (button_connect.isOver(mouseX, mouseY)) {
    connect_bt = true;
  } else if (button_disconnect.isOver(mouseX, mouseY)) {
    disconnect_bt = true;
  }
  if (tab1) slider_servo.premuto = false;
  if (tab2 ) {
    slider_kp.premuto = false;
    slider_ki.premuto = false;
    slider_kd.premuto = false;
    slider_pid_setpoint.premuto = false;
    slider_turning_speed.premuto = false;
    
    if (button_send.isOver(mouseX, mouseY)) {
      send = true;
    }
  }
}

//PER USARE IL BLUETOOTH

void onCreate(Bundle savedInstanceState) {
  super.onCreate(savedInstanceState);
  bt = new KetaiBluetooth(this);
  println("Creating KetaiBluetooth");
}

//PER RICEVERE MESSAGGI BLUETOOTH

void onActivityResult(int requestCode, int resultCode, Intent data) {
  bt.onActivityResult(requestCode, resultCode, data);
}

//PER VISUALIZZARE I TOAST

void showToast(final String message) { 
  act.runOnUiThread(new Runnable() { 
    public void run() { 
      android.widget.Toast.makeText(act.getApplicationContext(), message, android.widget.Toast.LENGTH_SHORT).show();
    }
  }
  );
}