package remote;

import processing.core.*;

/**
 * Joystick used for the Processing app for Android for self balancing robot.
 * It acts as a remote controller.
 * Note: the "joystick" is the static part, the "cursor" is the part that follows the mouse/finger
 */

public class Joystick implements InputDrawable {
    private PApplet p;
    private PVector cursorAbsPosition, pMouse, cursorRelativePosition;
    private boolean following;

    public float R, r;
    public int joystickColor, cursorDefaultColor, cursorPushedColor;
    public int minYValue, maxYValue, minXValue, maxXValue;
    public PVector joystickCenter;

    private void setup(PApplet main, float jx, float jy, float joystickRadius, float cursorRadius) {
        p = main;
        joystickCenter = new PVector(jx, jy);
        cursorAbsPosition = joystickCenter.copy();
        cursorRelativePosition = new PVector(0, 0);
        R = joystickRadius;
        r = cursorRadius;

        pMouse = new PVector();

        minYValue = minXValue = -4;
        maxYValue = maxXValue = 4;
        joystickColor = 255;
        cursorDefaultColor = 200;
        cursorPushedColor = 100;
        following = false;
    }

    /**
     * Create a new joystick. It defaults at x = width/2, y = height/2, with a radius of 
     * 3/8 and 3/32 of the width/height (the smaller one)
     * @param main the main PApplet sketch
     */
    public Joystick(PApplet main) {
        float radius = main.width <= main.height ? main.width / 8 * 3 : main.height / 8 * 3;
        setup(main, main.width / 2, main.height / 2, radius, radius / 4);
    }

    /**
     * Create a new joystick with the specified parameters.
     * @param main the main PApplet sketch
     * @param jx the joystick center position (x)
     * @param jy the joystick center position (y)
     * @param joystickRadius the radius of the joystick
     * @param cursorRadius the radius of the cursor
     */
    public Joystick(PApplet main, float jx, float jy, float joystickRadius, float cursorRadius) {
        setup(main, jx, jy, joystickRadius, cursorRadius);
    }

    /**
     * Update the joystick. The drawing part is not included, it has to be handled by yourself.
     */
    public void update() {
        pMouse.x = p.mouseX;
        pMouse.y = p.mouseY;

        // cursorRelativePosition = PVector.sub(pMouse, joystickCenter);
        cursorRelativePosition.x = 0;
        cursorRelativePosition.y = 0;

        if (p.mousePressed) {
            if (isOver(pMouse)) {
                following = true;
                cursorRelativePosition = PVector.sub(pMouse, joystickCenter);
            } else if (following) {
                cursorRelativePosition = PVector.sub(pMouse, joystickCenter);
            }
        } else {
            following = false;
        }

        cursorAbsPosition = PVector.add(joystickCenter, cursorRelativePosition.limit(R));
    }

    private boolean isOver(PVector mouse) {
        return mouse.dist(joystickCenter) <= R;
    }

    /**
     * Return wether the cursor is being dragged or not.
     * @return true if the cursor is being dragged, else false
     */
    public boolean isFollowing() {
        return following;
    }

    /**
     * Get the position of the cursor relative to the joystick.
     * @return a PVector containing the relative position of the cursor
     */
    public PVector getCursorRelativePosition() {
        return cursorRelativePosition;
    }

    /**
     * Get the position of the cursor in absolute value.
     * @return a PVector containing the position of the cursor
     */
    public PVector getCursorAbsPosition() {
        return cursorAbsPosition;
    }

    /**
     * Get the y value needed.
     * @return the value.
     */
    public int getYPower() {
        return Math.round(p.map(-cursorRelativePosition.y, -R, R, minYValue, maxYValue));
    }

    /**
     * Get the x value needed.
     * @return the value.
     */
    public int getXPower() {
        return Math.round(p.map(-cursorRelativePosition.y, -R, R, minXValue, maxXValue));
    }

    @Override
    public void updateDraw() {
        p.ellipseMode(PConstants.RADIUS);
        p.fill(joystickColor);
        p.stroke(0);
        p.strokeWeight(1);
        p.ellipse(joystickCenter.x, joystickCenter.y, R, R);
        p.fill(isFollowing() ? cursorPushedColor : cursorDefaultColor);
        p.ellipse(getCursorAbsPosition().x, getCursorAbsPosition().y, r, r);
    }

    @Override
    public boolean isBeingUsed() {
        return following;
    }
}