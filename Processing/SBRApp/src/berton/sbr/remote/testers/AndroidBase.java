package berton.sbr.remote.testers;

import processing.core.PApplet;
import berton.sbr.remote.drawables.*;

public class AndroidBase extends PApplet {
    private TabManager tabManager;
    private Joystick joystick;
    private Slider cameraSlider, kpSlider, kdSlider, kiSlider, setpointSlider, turnSpeedSlider;
    private Button connectBtn, disconnectBtn, sendBtn;
    private Led connectedLed;

    //Diagnostics
    private long start;
    private int sum = 0;
    private float avg;
    private static final int nSamples = 60;
    private int count = 0;

    //Simulation like settings
    private long time;
    private static final int delayToConnect = 1000;
    private boolean waitingToConnect = false;

    public static void main(String[] args) {
        PApplet.main("berton.sbr.remote.testers.AndroidBase");
    }

    public void settings() {
        size(270, 570);
    }

    public void setup() {
        frameRate(60);
        kpSlider = new Slider(this);
        tabManager = new TabManager(this);
        tabManager.addTab("Remote");
        tabManager.addTab("Settings");

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

        tabManager.insertDrawable(joystick, 0);
        tabManager.insertDrawable(cameraSlider, 0);
        tabManager.insertDrawable(connectBtn, 0);
        tabManager.insertDrawable(disconnectBtn, 0);
        tabManager.insertDrawable(connectedLed, 0);

        tabManager.insertDrawable(connectBtn, 1);
        tabManager.insertDrawable(connectedLed, 1);
        tabManager.insertDrawable(disconnectBtn, 1);
        tabManager.insertDrawable(kpSlider, 1);
        tabManager.insertDrawable(kiSlider, 1);
        tabManager.insertDrawable(kdSlider, 1);
        tabManager.insertDrawable(setpointSlider, 1);
        tabManager.insertDrawable(turnSpeedSlider, 1);
        tabManager.insertDrawable(sendBtn, 1);

        start = System.currentTimeMillis();
    }

    public void draw() {
        background(160);
        tabManager.update();
        tabManager.updateDraw();

        if (connectBtn.onActivated()) {
            if (!connectedLed.activated && !waitingToConnect) {
                time = System.currentTimeMillis();
                waitingToConnect = true;
            }
        }
        if (waitingToConnect && System.currentTimeMillis() > time + delayToConnect) {
            waitingToConnect = false;
            connectedLed.activated = true;
            kpSlider.setValue(4);
        }
        if (disconnectBtn.onActivated())
            connectedLed.activated = waitingToConnect = false;

        if (sendBtn.onActivated()) {
            System.out.println("Send");
        }

        count++;
        sum += System.currentTimeMillis() - start;
        if (count >= nSamples) {
            avg = (float) sum / count;
            count = sum = 0;
            System.out.print("\r" + avg + " ms                 ");
        }
        start = System.currentTimeMillis();
    }

    public void mousePressed() {
        // System.out.println(mouseX + ", " + mouseY);
    }
}