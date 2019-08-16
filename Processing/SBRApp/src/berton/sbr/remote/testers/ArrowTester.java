package berton.sbr.remote.testers;

import berton.sbr.remote.drawables.ArrowButton;
import processing.core.PApplet;

public class ArrowTester extends PApplet {
    private ArrowButton a;

    public static void main(String[] args) {
        PApplet.main("berton.sbr.remote.testers.ArrowTester");
    }

    public void settings() {
        size(600, 600);
    }

    public void setup() {
        a = new ArrowButton(this, ArrowButton.ARROWDOWN);
    }

    public void draw() {
        background(100);
        a.update();
        a.updateDraw();
        if (a.isBeingUsed())
            System.out.println("Attivata");
    }
}