package berton.sbr.remote;

import processing.core.PApplet;

import java.util.LinkedList;
import java.util.Scanner;
import berton.sbr.remote.drawables.*;
import berton.sbr.remote.HC05Threaded;
import berton.sbr.remote.HC05;

public class PCApp extends PApplet {
    private HC05 hc05;
    private TabManager tabManager;
    private Button connectButton, disconnectButton, sendButton;
    private TextBoxDisplay texts;
    private Joystick joystick;
    private Slider cameraSlider, kpSlider, kdSlider, kiSlider, setpointSlider, turnSpeedSlider;
    private Led connectedLed;

    long start;
    long time;
    int count;
    double avg;

    public static void main(String[] args) {
        PApplet.main("berton.sbr.remote.PCApp");
    }

    public void settings() {
        size(1000, 500);
    }

    public void setup() {
        frameRate(60);
        // hc05 = new HC05Threaded();      
        hc05 = new HC05();
        tabManager = new TabManager(this);
        tabManager.charDimension = 20;
        tabManager.addTab("Remote");
        tabManager.addTab("Settings");
        connectButton = new Button(this, 100, 70, "CONNECT", 25);
        disconnectButton = new Button(this, connectButton.pos.x + connectButton.size.x + 50, connectButton.pos.y,
                "DISCONNECT", 25);
        connectedLed = new Led(this, disconnectButton.pos.x + disconnectButton.size.x / 2 + 50, disconnectButton.pos.y,
                50);
        cameraSlider = new Slider(this, 200, connectButton.pos.y + connectButton.size.y / 2 + 70, 300, 50);
        texts = new TextBoxDisplay(this, width / 2, connectButton.pos.y - connectButton.size.y / 2, width / 2 - 20,
                (height - tabManager.getLastY()) - 50);
        texts.setMaxLines(30);
        texts.insertLine("test");
        joystick = new Joystick(this, 210, height / 2 + 80, 100, 20);
        joystick.maxXValue = joystick.maxYValue = 8;
        joystick.minXValue = joystick.minYValue = 0;

        cameraSlider.maxValue = 180;
        cameraSlider.title = "Camera";
        cameraSlider.setValue(90);

        tabManager.insertDrawable(connectButton, 0);
        tabManager.insertDrawable(disconnectButton, 0);
        tabManager.insertDrawable(connectedLed, 0);
        tabManager.insertDrawable(connectButton, 1);
        tabManager.insertDrawable(disconnectButton, 1);
        tabManager.insertDrawable(connectedLed, 1);
        tabManager.insertDrawable(cameraSlider, 0);
        tabManager.insertDrawable(texts, 0);
        tabManager.insertDrawable(joystick, 0);

        kpSlider = new Slider(this, 250, 150, 400, 40);
        kiSlider = new Slider(this, kpSlider.sliderPos.x, kpSlider.sliderPos.y + kpSlider.sliderSize.y + 30,
                kpSlider.sliderSize.x, kpSlider.sliderSize.y);
        kdSlider = new Slider(this, kiSlider.sliderPos.x, kiSlider.sliderPos.y + kiSlider.sliderSize.y + 30,
                kiSlider.sliderSize.x, kiSlider.sliderSize.y);
        setpointSlider = new Slider(this, kdSlider.sliderPos.x, kdSlider.sliderPos.y + kdSlider.sliderSize.y + 30,
                kdSlider.sliderSize.x, kdSlider.sliderSize.y);
        turnSpeedSlider = new Slider(this, setpointSlider.sliderPos.x,
                setpointSlider.sliderPos.y + setpointSlider.sliderSize.y + 30, setpointSlider.sliderSize.x,
                setpointSlider.sliderSize.y);
        sendButton = new Button(this, width / 4 * 3, height / 2, "SEND", 40);

        kpSlider.title = "Kp";
        kpSlider.maxValue = 20;
        kiSlider.title = "Ki";
        kiSlider.maxValue = 20;
        kdSlider.title = "Kd";
        kdSlider.maxValue = 20;
        setpointSlider.title = "Setpoint";
        setpointSlider.maxValue = 30;
        turnSpeedSlider.title = "Turning speed";
        turnSpeedSlider.maxValue = 300;

        tabManager.insertDrawable(kpSlider, 1);
        tabManager.insertDrawable(kiSlider, 1);
        tabManager.insertDrawable(kdSlider, 1);
        tabManager.insertDrawable(setpointSlider, 1);
        tabManager.insertDrawable(turnSpeedSlider, 1);
        tabManager.insertDrawable(sendButton, 1);

        start = System.currentTimeMillis();
        time = start;
        // hc05.start();
    }

    public void draw() {
        background(150);

        tabManager.update();
        tabManager.updateDraw();

        connectedLed.activated = hc05.isConnected();

        if (connectButton.onActivated()) {
            tabManager.showToast("Connecting...");
            hc05.connect();
        }

        if (hc05.onConnect()) {
            tabManager.showToast("Connected!");
            texts.clear();
        }

        if (disconnectButton.onActivated()) {
            try {
                tabManager.showToast("Disconnecting...");
                hc05.disconnect();
                tabManager.showToast("Disconnected!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (hc05.isConnected()) {
            try {
                String recv = hc05.getStringFromHC05();
                if (!recv.equals("")) { // if we actually received data...
                    if (texts.getNumberOfLines() == 1)
                        hc05.sendString("e;");
                    texts.insertLine(recv);
                }

                if (recv.startsWith("e")) {
                    System.out.println("TROVATO: " + recv + "!!!");
                    Scanner scanner = new Scanner(recv.substring(1));
                    scanner.useDelimiter(" |,|;");
                    kpSlider.setValue(scanner.nextFloat());
                    kiSlider.setValue(scanner.nextFloat());
                    kdSlider.setValue(scanner.nextFloat());
                    setpointSlider.setValue(scanner.nextFloat());
                    turnSpeedSlider.setValue(scanner.nextFloat());
                    scanner.close();
                }

                if (tabManager.activeTab == 0) {
                    hc05.sendString("v" + joystick.getYPower() + "" + joystick.getXPower() + ";");
                    hc05.sendString("c" + cameraSlider.getValue() + ";");
                }
                if (tabManager.activeTab == 1) {
                    if (sendButton.onActivated()) {
                        hc05.sendString(
                                "k" + kpSlider.getValue() + "," + kiSlider.getValue() + "," + kdSlider.getValue());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        avg += System.currentTimeMillis() - time;
        count++;
        time = System.currentTimeMillis();

        if (System.currentTimeMillis() > start + 1000) {
            avg = avg / count;
            // System.out.print("\r" + String.format("%.2f", avg) + " ms - " + String.format("%.2f", 1000 / avg));
            avg = count = 0;
            start = System.currentTimeMillis();
            time = start;
        }
    }

    public void mousePressed() {
        // System.out.println(mouseX + ", " + mouseY);
    }
}