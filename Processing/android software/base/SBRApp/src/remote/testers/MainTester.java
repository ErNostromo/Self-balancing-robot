package remote.testers;

import processing.core.PApplet;
import remote.*;

public class MainTester extends PApplet {
    private TabManager t;
    private Joystick j;
    private Slider s1, s2;
    private CheckBox c1, c2;
    private Button b1, b2;
    private Led l1, l2;

    public static void main(String[] args) {
        PApplet.main("remote.testers.MainTester");
    }

    public void settings() {
        size(270, 570);
    }

    public void setup() {
        t = new TabManager(this);
        t.addTab("Tab1");
        t.addTab("Tabbbb2");
        t.addTab("Tababababa     3");
        t.addTab("aaaaaaaaaaaaaaaaaaaaaaa");

        j = new Joystick(this, width / 2, height - 200, 100, 20);
        s1 = new Slider(this, 75, 100, 100, 30);
        s2 = new Slider(this, 270 - 75, 100, 100, 30);
        b1 = new Button(this, 75, 150, "Pulsante 1", 16);
        b2 = new Button(this, 270 - 75, 150, "Pulsante 2", 20);
        c1 = new CheckBox(this, 75, 200, 50);
        c2 = new CheckBox(this, 270 - 75, 200, 50);
        l1 = new Led(this, 50, 510, 70);
        l2 = new Led(this, 220, 510, 70);

        t.insertDrawable(j, 0);
        t.insertDrawable(s1, 0);
        t.insertDrawable(s2, 0);
        t.insertDrawable(b1, 0);
        t.insertDrawable(b2, 0);
        t.insertDrawable(c1, 0);
        t.insertDrawable(c2, 0);
        t.insertDrawable(l1, 0);
        t.insertDrawable(l2, 0);
    }

    public void draw() {
        background(127);
        t.update();
        t.updateDraw();

        l1.activated = b1.isTriggered();
        l2.activated = c2.isTriggered();
    }

    public void mousePressed() {
        // System.out.println(mouseX + ", " + mouseY);
    }
}