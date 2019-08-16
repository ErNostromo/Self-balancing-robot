package berton.sbr.remote;

import processing.core.PApplet;

import berton.sbr.remote.drawables.*;

public class PCApp extends PApplet {
    private HC05 hc05;
    private TabManager tabManager;
    private Button connectButton;
    private Button disconnectButton;
    private TextBoxDisplay texts;
    private Joystick joystick;

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
        tabManager.insertDrawable(texts, 0);
        tabManager.insertDrawable(joystick, 0);

        time = System.currentTimeMillis();
    }

    public void draw() {
        background(100);

        tabManager.update();
        tabManager.updateDraw();

        if (connectButton.onActivated() && !hc05.isConnected()) {
            try {
                if (hc05.connect())
                    System.out.println("Connected!");
                else
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

                hc05.sendString("v" + joystick.getYPower() + "" + joystick.getXPower() + ";");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        sum += System.currentTimeMillis() - time;
        count++;
        if (count >= samples) {
            avg = sum / (float) samples;
            System.out.println("\r\n" + avg + "ms");
            sum = count = 0;
        }
        time = System.currentTimeMillis();
    }

    public void mousePressed() {
        // System.out.println(mouseX + ", " + mouseY);
    }
}