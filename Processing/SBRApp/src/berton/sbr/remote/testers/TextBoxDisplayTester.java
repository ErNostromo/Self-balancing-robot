package berton.sbr.remote.testers;

import processing.core.PApplet;
import berton.sbr.remote.drawables.TextBoxDisplay;

public class TextBoxDisplayTester extends PApplet {
    private TextBoxDisplay t;
    private long time;
    private int count;

    public static void main(String[] args) {
        PApplet.main("berton.sbr.remote.testers.TextBoxDisplayTester");
    }

    public void setup() {
        t = new TextBoxDisplay(this);
        t.setMaxLines(30);
        t.insertLine("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        time = System.currentTimeMillis();
    }

    public void settings() {
        size(600, 600);
    }

    public void draw() {
        background(100);

        t.update();
        t.updateDraw();
        if (System.currentTimeMillis() > time + 1000) {
            t.insertLine(Integer.toString(count++));
            time = System.currentTimeMillis();
        }
    }
}