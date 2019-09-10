package berton.sbr.remote.drawables;

import java.util.Iterator;
import java.util.LinkedList;

import processing.core.PApplet;
import processing.core.PConstants;

public class ToastManager implements Drawable {
    private PApplet p;
    private LinkedList<String> toasts;
    private Iterator<String> iterator;
    private String currentText;
    private long lastTime;
    private int charDimension;
    private boolean showingToast;
    private boolean enableUpdate;

    public int mainColor;
    public int millisDuration;
    public int millisWait;

    private void setup(PApplet sketch) {
        p = sketch;
        toasts = new LinkedList<String>();
        iterator = toasts.iterator();

        charDimension = 16;
        showingToast = false;
        enableUpdate = true;
        mainColor = 127;
        millisDuration = 2000;
        millisWait = 500;
    }

    public ToastManager(PApplet sketch) {
        setup(sketch);
    }

    public void addToast(String text) {
        toasts.add(text);
    }

    @Override
    public void update() {
        if (iterator.hasNext()) {
            if (enableUpdate && !showingToast) {
                currentText = toasts.removeFirst();
                showingToast = true;
                enableUpdate = false;
                lastTime = System.currentTimeMillis();
            }
        }
        if (showingToast && System.currentTimeMillis() >= lastTime + millisDuration) {
            showingToast = false;
            lastTime = System.currentTimeMillis();
        }
        if (!showingToast && !enableUpdate) {
            if (System.currentTimeMillis() >= lastTime + millisWait) {
                enableUpdate = true;
            }
        }
    }

    @Override
    public void updateDraw() {
        if (showingToast) {
            float rectWidth = p.textWidth(currentText);
            float radius = charDimension + 10;
            float y = p.height - radius - 10;
            p.noStroke();
            p.ellipseMode(PConstants.RADIUS);
            p.fill(mainColor);
            p.ellipse(p.width / 2 - rectWidth / 2, y, radius, radius);
            p.ellipse(p.width / 2 + rectWidth / 2, y, radius, radius);
            p.rectMode(PConstants.CENTER);
            p.rect(p.width / 2, y, rectWidth, radius * 2);

            p.fill(0);
            p.textSize(charDimension);
            p.textAlign(PConstants.CENTER, PConstants.CENTER);
            p.text(currentText, p.width / 2, y);
        }
    }
}