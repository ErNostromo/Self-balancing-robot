package remote.testers;

import processing.core.PApplet;
import remote.CheckBox;

public class CheckBoxTester extends PApplet {
    CheckBox c;

    public static void main(String[] args) {
        PApplet.main("remote.testers.CheckBoxTester");
    }

    public void settings() {
        size(600, 600);
    }

    public void setup() {
        c = new CheckBox(this);
    }

    public void draw() {
        background(127);
        c.update();
        c.updateDraw();
    }
}