package remote.testers;

import processing.core.*;
import remote.Joystick;

public class JoystickTester extends PApplet {
    private Joystick j1, j2;
    private long t;

    public static void main(String[] args) {
        PApplet.main("pad.Test");
    }

    public void settings() {
        size(300, 600);
    }

    public void setup() {
        System.out.println("bruh");
        j1 = new Joystick(this, width / 2, 150, 100, 20);
        j2 = new Joystick(this, width / 2, 450, 70, 15);

        j1.joystickColor = j2.joystickColor = 255;
        j1.cursorDefaultColor = j2.cursorDefaultColor = 200;
        j1.cursorPushedColor = j2.cursorPushedColor = 50;

        frameRate(60);

        t = System.currentTimeMillis();
    }

    public void draw() {
        background(127);

        j1.update();
        j2.update();

        drawObject(j1);
        drawObject(j2);

        // System.out.print(j.getYPower());
        // System.out.println();

        if (System.currentTimeMillis() > t + 500) {
            // System.out.print(j.getX() + "; " + j.getY());
            t = System.currentTimeMillis();
        }
    }

    public void drawObject(Joystick j) {
        ellipseMode(RADIUS);
        fill(j.joystickColor);
        stroke(0);
        strokeWeight(1);
        ellipse(j.joystickCenter.x, j.joystickCenter.y, j.R, j.R);
        fill(j.isFollowing() ? j.cursorPushedColor : j.cursorDefaultColor);
        ellipse(j.getCursorAbsPosition().x, j.getCursorAbsPosition().y, j.r, j.r);
    }
}