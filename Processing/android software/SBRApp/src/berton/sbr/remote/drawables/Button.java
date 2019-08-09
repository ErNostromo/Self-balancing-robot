package berton.sbr.remote.drawables;

import processing.core.*;

public class Button implements InputDrawable {
    protected PApplet p;
    protected int state;
    protected boolean activated;
    protected boolean triggered;

    public String name;
    public PVector pos, size;
    public int buttonDefaultColor, buttonActivatedColor;
    public float textSize;

    private void setup(PApplet main, float cx, float cy, float w, float h) {
        p = main;
        pos = new PVector(cx, cy);
        size = new PVector(w, h);

        buttonDefaultColor = 200;
        buttonActivatedColor = 100;
        state = 0;
        textSize = 16;
        name = "";
    }

    /**
     * Creates a new button with default parameters.
     * @param main the main PApplet sketch
     */
    public Button(PApplet main) {
        setup(main, main.width / 2, main.height / 2, main.width / 8, main.width / 16);
    }

    /**
     * Create a new button
     * @param main the main PApplet sketch
     * @param cx x coord of center
     * @param cy y coord of center
     * @param name name to display on the button
     * @param textSize size of the name (directly sets textSize variable). It also determines width and height of the button
     */
    public Button(PApplet main, float cx, float cy, String name, int textSize) {
        setup(main, cx, cy, 0, 0);
        this.textSize = textSize;
        setName(name);
        adjustSizeToFitName();
    }

    /**
     * Create a new button
     * @param main main PApplet sketch
     * @param cx x coord of center
     * @param cy y coord of center
     * @param w width of button
     * @param h height of button
     */
    public Button(PApplet main, float cx, float cy, float w, float h) {
        setup(main, cx, cy, w, h);
    }

    /**
     * Set the name to display on the button. It is suggested to adjustSizeToFitName() after this.
     * @param name name to display
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets width and height of the button according to the textSize variable
     */
    public void adjustSizeToFitName() {
        p.textSize(textSize);
        size.x = p.textWidth(name) + 20;
        size.y = textSize + textSize / 3 * 2;
    }

    public void update() {
        switch (state) {
        case 0:
            activated = triggered = false;
            if (p.mousePressed && isOver(p.mouseX, p.mouseY))
                ++state;
            break;
        case 1:
            activated = true;
            triggered = false;
            if (isOver(p.mouseX, p.mouseY)) {
                if (!p.mousePressed)
                    ++state;
            } else
                --state;
            break;
        case 2:
            activated = false;
            triggered = true;
            if (p.mousePressed && isOver(p.mouseX, p.mouseY))
                ++state;
            break;
        case 3:
            activated = true;
            triggered = true;
            if (isOver(p.mouseX, p.mouseY)) {
                if (!p.mousePressed)
                    ++state;
            } else
                --state;
            break;

        default:
            state = 0;
            break;
        }
    }

    private boolean isOver(float x, float y) {
        return x > pos.x - size.x / 2 && x < pos.x + size.x / 2 && y > pos.y - size.y / 2 && y < pos.y + size.y / 2;
    }

    public boolean isActivated() {
        return activated;
    }

    public boolean isTriggered() {
        return triggered;
    }

    @Override
    public void updateDraw() {
        p.rectMode(PConstants.CENTER);
        p.fill(activated ? buttonActivatedColor : buttonDefaultColor);
        p.stroke(0);
        p.strokeWeight(1);
        p.rect(pos.x, pos.y, size.x, size.y);
        p.textAlign(PConstants.CENTER, PConstants.CENTER);

        p.fill(0);
        p.textSize(textSize);
        p.text(name, pos.x, pos.y);
    }

    @Override
    public boolean isBeingUsed() {
        return activated;
    }
}