package berton.sbr.remote.drawables;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;

public class ArrowButton implements InputDrawable {
    public static final int ARROWUP = 0;
    public static final int ARROWDOWN = 1;
    public static final int ARROWLEFT = 2;
    public static final int ARROWRIGHT = 3;

    private static final float arrowOffsetFactor = 1 / 6;
    private PVector upPos, downPos, leftPos, rightPos;
    private PApplet p;
    private boolean activated;

    public PVector pos;
    public float size;
    public int type;
    public int defaultColor;
    public int activatedColor;

    public void setup(PApplet main, float cx, float cy, float size, int type) {
        p = main;
        pos = new PVector(cx, cy);
        this.size = size;
        activated = false;
        this.type = type;

        upPos = new PVector();
        downPos = new PVector();
        leftPos = new PVector();
        rightPos = new PVector();
        defaultColor = 200;
        activatedColor = 127;
    }

    public ArrowButton(PApplet main, int type) {
        setup(main, main.width / 2, main.height / 2, 50, type);
    }

    public ArrowButton(PApplet main, float cx, float cy, float size, int type) {
        setup(main, cx, cy, size, type);
    }

    public boolean isOver(float x, float y) {
        return x > pos.x - size / 2 && x < pos.x + size / 2 && y > pos.y - size / 2 && y < pos.y + size / 2;
    }

    @Override
    public boolean isBeingUsed() {
        return activated;
    }

    public boolean isActivated() {
        return activated;
    }

    @Override
    public void update() {
        if (p.mousePressed && isOver(p.mouseX, p.mouseY)) {
            activated = true;
        } else
            activated = false;
    }

    @Override
    public void updateDraw() {
        p.rectMode(PConstants.CENTER);
        p.fill(activated ? activatedColor : defaultColor);
        p.stroke(0);
        p.strokeWeight(0);
        p.rect(pos.x, pos.y, size, size);

        upPos.x = pos.x;
        downPos.x = pos.x;
        leftPos.x = pos.x - size / 2 + size * arrowOffsetFactor;
        rightPos.x = pos.x - size / 2 + size - size * arrowOffsetFactor;

        upPos.y = pos.y - size / 2 + size * arrowOffsetFactor;
        downPos.y = pos.y - size / 2 + size - size * arrowOffsetFactor;
        leftPos.y = pos.y;
        rightPos.y = pos.y;
        p.strokeWeight(1);
        switch (type) {
        case ARROWUP:
            p.line(leftPos.x, leftPos.y, upPos.x, upPos.y);
            p.line(rightPos.x, rightPos.y, upPos.x, upPos.y);
            break;

        case ARROWDOWN:
            p.line(leftPos.x, leftPos.y, downPos.x, downPos.y);
            p.line(rightPos.x, rightPos.y, downPos.x, downPos.y);
            break;

        case ARROWLEFT:
            p.line(upPos.x, upPos.y, leftPos.x, leftPos.y);
            p.line(downPos.x, downPos.y, leftPos.x, leftPos.y);
            break;

        case ARROWRIGHT:
            p.line(upPos.x, upPos.y, rightPos.x, rightPos.y);
            p.line(downPos.x, downPos.y, rightPos.x, rightPos.y);
            break;

        default:
            throw new RuntimeException("Type not supported (" + type + ")");
        }
    }
}