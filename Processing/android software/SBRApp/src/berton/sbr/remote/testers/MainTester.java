package berton.sbr.remote.testers;

import processing.core.PApplet;
import berton.sbr.remote.drawables.*;

public class MainTester extends PApplet {
    private TabManager t;
    private Joystick joystick;
    private Slider cameraSlider, kpSlider, kdSlider, kiSlider, setpointSlider, turnSpeedSlider;
    private Button connectBtn, disconnectBtn, sendBtn;
    private Led connectedLed;

    public static void main(String[] args) {
        PApplet.main("berton.sbr.remote.testers.MainTester");
    }

    public void settings() {
        size(270, 570);
    }

    public void setup() {
        frameRate(60);
        kpSlider = new Slider(this);
        t = new TabManager(this);
        t.addTab("Remote");
        t.addTab("Settings");

        joystick = new Joystick(this, width / 2, height - 200, 100, 20);
        connectBtn = new Button(this, 50, 60, "Connect", 16);
        disconnectBtn = new Button(this, 140, connectBtn.pos.y, "Disconnect", 16);
        connectedLed = new Led(this, 230, connectBtn.pos.y, 40);
        cameraSlider = new Slider(this, 110, 130, 200, 30);

        kpSlider = new Slider(this, cameraSlider.sliderPos.x, cameraSlider.sliderPos.y, cameraSlider.sliderSize.x,
                cameraSlider.sliderSize.y);
        kiSlider = new Slider(this, cameraSlider.sliderPos.x, kpSlider.sliderPos.y + kpSlider.sliderSize.y + 30,
                cameraSlider.sliderSize.x, cameraSlider.sliderSize.y);
        kdSlider = new Slider(this, cameraSlider.sliderPos.x, kiSlider.sliderPos.y + kpSlider.sliderSize.y + 30,
                cameraSlider.sliderSize.x, cameraSlider.sliderSize.y);
        setpointSlider = new Slider(this, cameraSlider.sliderPos.x, kdSlider.sliderPos.y + kpSlider.sliderSize.y + 30,
                cameraSlider.sliderSize.x, cameraSlider.sliderSize.y);
        turnSpeedSlider = new Slider(this, cameraSlider.sliderPos.x,
                setpointSlider.sliderPos.y + kpSlider.sliderSize.y + 30, cameraSlider.sliderSize.x,
                cameraSlider.sliderSize.y);
        sendBtn = new Button(this, width / 2, height - 100, "Send", 18);

        cameraSlider.setTitle("Camera");
        cameraSlider.minValue = 0;
        cameraSlider.maxValue = 180;
        kpSlider.setTitle("Kp");
        kpSlider.minValue = 0;
        kpSlider.maxValue = 10;
        kiSlider.setTitle("Ki");
        kiSlider.minValue = 0;
        kiSlider.maxValue = 10;
        kdSlider.setTitle("Kd");
        kdSlider.minValue = 0;
        kdSlider.maxValue = 10;
        setpointSlider.setTitle("Setpoint");
        setpointSlider.minValue = 0;
        setpointSlider.maxValue = 10;
        turnSpeedSlider.setTitle("Turning speed");
        turnSpeedSlider.minValue = 0;
        turnSpeedSlider.maxValue = 10;
        sendBtn.size.x += 40;

        t.insertDrawable(joystick, 0);
        t.insertDrawable(cameraSlider, 0);
        t.insertDrawable(connectBtn, 0);
        t.insertDrawable(disconnectBtn, 0);
        t.insertDrawable(connectedLed, 0);

        t.insertDrawable(connectBtn, 1);
        t.insertDrawable(connectedLed, 1);
        t.insertDrawable(disconnectBtn, 1);
        t.insertDrawable(kpSlider, 1);
        t.insertDrawable(kiSlider, 1);
        t.insertDrawable(kdSlider, 1);
        t.insertDrawable(setpointSlider, 1);
        t.insertDrawable(turnSpeedSlider, 1);
        t.insertDrawable(sendBtn, 1);
    }

    public void draw() {
        background(127);
        t.update();
        t.updateDraw();

        connectedLed.activated = connectBtn.isTriggered();
    }

    public void mousePressed() {
        // System.out.println(mouseX + ", " + mouseY);
    }
}