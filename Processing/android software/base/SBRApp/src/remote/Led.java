package remote;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;

public class Led implements Drawable {
    private PApplet p;
    private long time;

    public boolean activated;
    public PVector pos;
    public float r;
    public int onColor, offColor;
    public int delay;
    public boolean blinking;

    public void setup(PApplet main, float x, float y, float radius) {
        p = main;
        pos = new PVector(x, y);
        r = radius;

        onColor = p.color(0, 255, 0);
        offColor = p.color(255, 0, 0);
        delay = 1000;
        blinking = false;
    }

    public Led(PApplet main) {
        setup(main, main.width / 2, main.height / 2, main.width < main.height ? main.width / 8 : main.height / 8);
    }

    public Led(PApplet main, float x, float y, float radius) {
        setup(main, x, y, radius);
    }

    @Override
    public void update() {
        if (blinking && System.currentTimeMillis() > time + delay) {
            activated = !activated;
            time = System.currentTimeMillis();
        }
    }

    @Override
    public void updateDraw() {
        p.ellipseMode(PConstants.CENTER);
        p.fill(activated ? onColor : offColor);
        p.ellipse(pos.x, pos.y, r, r);
    }
}