package remote.testers;

import processing.core.PApplet;
import remote.Led;

public class LedTester extends PApplet {
    private Led led;
    private long time;

    public static void main(String[] args) {
        PApplet.main("remote.testers.LedTester");
    }

    public void settings() {
        size(600, 600);
    }

    public void setup() {
        led = new Led(this);
        time = System.currentTimeMillis();
    }

    public void draw() {
        background(127);
        if (System.currentTimeMillis() > time + 1000) {
            led.activated = !led.activated;
            time = System.currentTimeMillis();
        }

        led.update();
        led.updateDraw();
    }
}