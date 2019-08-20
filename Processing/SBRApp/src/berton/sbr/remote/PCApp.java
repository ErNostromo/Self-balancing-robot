package berton.sbr.remote;

import processing.core.PApplet;

import berton.sbr.remote.drawables.*;

import java.util.Scanner;

import berton.sbr.HC05;

public class PCApp extends PApplet {
    private HC05 hc05;
    private TabManager tabManager;
    private Button connectButton, disconnectButton, sendButton;
    private TextBoxDisplay texts;
    private Joystick joystick;
    private Slider cameraSlider, kpSlider, kdSlider, kiSlider, setpointSlider, turnSpeedSlider;

    // Diagnostics
    private long time;
    private static final int samples = 100;
    private int count;
    private int sum;
    private float avg;

    public static void main(String[] args) {
        PApplet.main("berton.sbr.remote.PCApp");
    }

    public void settings() {
        size(1000, 500);
    }

    public void setup() {
        frameRate(9999); // The fastest, the better
        hc05 = new HC05();
        tabManager = new TabManager(this);
        tabManager.charDimension = 20;
        tabManager.addTab("Remote");
        tabManager.addTab("Settings");
        connectButton = new Button(this, 100, 70, "CONNECT", 30);
        disconnectButton = new Button(this, connectButton.pos.x + connectButton.size.x + 50, connectButton.pos.y,
                "DISCONNECT", 30);
        texts = new TextBoxDisplay(this, width / 2, connectButton.pos.y - connectButton.size.y / 2, width / 2 - 20,
                (height - tabManager.getLastY()) - 50);
        texts.setMaxLines(30);
        texts.insertLine("test");
        joystick = new Joystick(this, 210, height / 2, 100, 20);
        joystick.maxXValue = joystick.maxYValue = 8;
        joystick.minXValue = joystick.minYValue = 0;

        tabManager.insertDrawable(connectButton, 0);
        tabManager.insertDrawable(disconnectButton, 0);
        tabManager.insertDrawable(connectButton, 1);
        tabManager.insertDrawable(disconnectButton, 1);
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

        kpSlider.setTitle("Kp");
        kpSlider.maxValue = 20;
        kiSlider.setTitle("Ki");
        kiSlider.maxValue = 20;
        kdSlider.setTitle("Kd");
        kdSlider.maxValue = 20;
        setpointSlider.setTitle("Setpoint");
        setpointSlider.maxValue = 30;
        turnSpeedSlider.setTitle("Turning speed");
        turnSpeedSlider.maxValue = 300;

        tabManager.insertDrawable(kpSlider, 1);
        tabManager.insertDrawable(kiSlider, 1);
        tabManager.insertDrawable(kdSlider, 1);
        tabManager.insertDrawable(setpointSlider, 1);
        tabManager.insertDrawable(turnSpeedSlider, 1);
        tabManager.insertDrawable(sendButton, 1);

        time = System.currentTimeMillis();
    }

    public void draw() {
        background(150);

        tabManager.update();
        tabManager.updateDraw();

        if (connectButton.onActivated() && !hc05.isConnected()) {
            try {
                if (hc05.connect()) {
                    System.out.println("Connected!");
                    texts.clear();
                    try {
                        hc05.sendString("e");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else
                    System.out.println("Connection went wrong");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (disconnectButton.onActivated() && hc05.isConnected()) {
            try {
                hc05.disconnect();
                System.out.println("Disconnected!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (hc05.isConnected()) {
            try {
                String recv = hc05.getStringFromHC05();
                texts.insertLine(recv);

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

                if (tabManager.activeTab == 0)
                    hc05.sendString("v" + joystick.getYPower() + "" + joystick.getXPower() + ";");
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

        sum += System.currentTimeMillis() - time;
        count++;
        if (count >= samples) {
            avg = sum / (float) samples;
            System.out.print("\r" + avg + "ms / ");
            System.out.format("%.2f", 1000 / avg);
            System.out.print(" fps");
            sum = count = 0;
        }
        time = System.currentTimeMillis();
    }

    public void mousePressed() {
        // System.out.println(mouseX + ", " + mouseY);
    }

    public void keyPressed() {
        System.out.println(key);
    }
}