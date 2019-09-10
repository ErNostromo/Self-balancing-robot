package berton.sbr.remote.drawables;

import java.util.Iterator;
import java.util.LinkedList;

import processing.core.PApplet;
import processing.core.PConstants;

public class ToastManager implements Drawable {
    private PApplet p;
    private LinkedList<String> toasts;
    private Iterator<String> iterator;
    private int state;
    private int currentTransparency;
    private String currentText;
    private long lastTime;

    public int mainColor;
    public int charDimension;
    public int millisDelay;

    private void setup(PApplet sketch) {
        p = sketch;
        toasts = new LinkedList<String>();
        iterator = toasts.iterator();

        currentTransparency = 0;
        mainColor = 127;
        charDimension = 16;
        millisDelay = 1000;
    }

    public ToastManager(PApplet sketch) {
        setup(sketch);
    }

    public void addToast(String text) {
        toasts.add(text);
    }

    @Override
    public void update() {

        switch (state) {
        // Waiting
        case 0:
            if (iterator.hasNext()) {
                currentText = toasts.removeFirst();
                System.out.println(currentText);
                state++;
                currentTransparency = 0;
            }
            break;

        // Appearing
        case 1:
            currentTransparency++;
            if (currentTransparency >= 255) {
                currentTransparency = 255;
                state++;
                lastTime = System.currentTimeMillis();
            }
            break;

        // Staying
        case 2:
            if (System.currentTimeMillis() >= lastTime + millisDelay) {
                state++;
            }
            break;

        // Disappearing
        case 3:
            currentTransparency--;
            if (currentTransparency <= 0) {
                currentTransparency = 0;
            }
            break;
        }
    }

    @Override
    public void updateDraw() {
        if (state==0) return;
        p.fill(mainColor);
        p.stroke(0);
        p.strokeWeight(0);
        p.ellipseMode(PConstants.RADIUS);
        p.tint(255, currentTransparency);
        p.ellipse(p.width/2, 300, 30,30);
    }
}