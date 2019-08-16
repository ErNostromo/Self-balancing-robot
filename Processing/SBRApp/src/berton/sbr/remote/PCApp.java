package berton.sbr.remote;

import processing.core.PApplet;

import java.io.IOException;

import berton.sbr.remote.drawables.*;

public class PCApp extends PApplet {
    private HC05 hc05;
    private Button connectButton;
    private Button disconnectButton;

    public static void main(String[] args) {
        PApplet.main("berton.sbr.remote.PCApp");
    }

    public void settings() {
        size(1000, 500);
    }

    public void setup() {
        hc05 = new HC05();
        connectButton = new Button(this, 100, 50, "CONNECT", 30);
        disconnectButton = new Button(this, connectButton.pos.x + connectButton.size.x + 50, connectButton.pos.y,
                "DISCONNECT", 30);
    }

    public void draw() {
        background(100);

        connectButton.update();
        disconnectButton.update();
        connectButton.updateDraw();
        disconnectButton.updateDraw();

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
                System.out.print("\r" + hc05.getStringFromHC05());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}