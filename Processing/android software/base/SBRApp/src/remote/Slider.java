package remote;

import processing.core.*;

public class Slider implements InputDrawable {
    private PApplet p;
    private boolean following;
    private float cursorMinLimit, cursorMaxLimit;

    public PVector sliderPos, sliderSize, cursorAbsolutePos, cursorSize;
    public float minValue, maxValue;
    public int cursorDefaultColor, sliderColor, cursorActivatedColor;

    private void setup(PApplet main, float x, float y, float sliderW, float sliderH) {
        p = main;
        sliderPos = new PVector(x, y);
        sliderSize = new PVector(sliderW, sliderH);

        cursorSize = new PVector(sliderH, sliderH);
        cursorMinLimit = sliderPos.x - sliderSize.x / 2 + cursorSize.x / 2;
        cursorMaxLimit = sliderPos.x + sliderSize.x / 2 - cursorSize.x / 2;
        cursorAbsolutePos = new PVector(cursorMinLimit, sliderPos.y);

        minValue = 0;
        maxValue = 100;
        cursorDefaultColor = 200;
        cursorActivatedColor = 100;
        sliderColor = 255;
    }

    public Slider(PApplet main) {
        setup(main, main.width / 2, main.height / 2, main.width / 4, main.height / 16);
    }

    /**
     * Create a new Slider
     * @param main main PApplet sketch
     * @param x x coord of center
     * @param y y coord of center
     * @param sliderW width of slider
     * @param sliderH height of slider
     */
    public Slider(PApplet main, float x, float y, float sliderW, float sliderH) {
        setup(main, x, y, sliderW, sliderH);
    }

    public void update() {
        if (p.mousePressed) {
            if (isOver(p.mouseX, p.mouseY)) {
                following = true;
                cursorAbsolutePos.x = p.mouseX;
            } else if (following) {
                cursorAbsolutePos.x = p.mouseX;
            }
        } else {
            following = false;
        }

        if (following) {
            if (p.mouseX < cursorMinLimit)
                cursorAbsolutePos.x = cursorMinLimit;
            else if (p.mouseX > cursorMaxLimit)
                cursorAbsolutePos.x = cursorMaxLimit;
        }
    }

    private boolean isOver(float x, float y) {
        return x > cursorAbsolutePos.x - cursorSize.x / 2 && x < cursorAbsolutePos.x + cursorSize.x / 2
                && y > cursorAbsolutePos.y - cursorSize.y / 2 && y < cursorAbsolutePos.y + cursorSize.y / 2;

    }

    public boolean isFollowing() {
        return following;
    }

    public float getValue() {
        return Math.round(p.map(cursorAbsolutePos.x, cursorMinLimit, cursorMaxLimit, minValue, maxValue));
    }

    @Override
    public void updateDraw() {
        p.rectMode(PConstants.CENTER);
        p.fill(sliderColor);
        p.stroke(0);
        p.strokeWeight(1);
        p.rect(sliderPos.x, sliderPos.y, sliderSize.x, sliderSize.y);
        p.fill(following ? cursorActivatedColor : cursorDefaultColor);
        p.rect(cursorAbsolutePos.x, cursorAbsolutePos.y, cursorSize.x, cursorSize.y);
    }

    @Override
    public boolean isBeingUsed() {
        return following;
    }
}