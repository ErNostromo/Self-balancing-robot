package remote;

import processing.core.PApplet;
import processing.core.PConstants;

public class CheckBox extends Button {
    public CheckBox(PApplet main) {
        super(main);
    }

    public CheckBox(PApplet main, float cx, float cy, float size) {
        super(main, cx, cy, size, size);
    }

    // update is the same

    @Override
    public void updateDraw() {
        p.rectMode(PConstants.CENTER);
        p.fill(255);
        p.stroke(0);
        p.strokeWeight(1);
        p.rect(pos.x, pos.y, size.x, size.x);
        if (triggered) {
            p.line(pos.x - size.x / 2, pos.y - size.x / 2, pos.x + size.x / 2, pos.y + size.x / 2);
            p.line(pos.x + size.x / 2, pos.y - size.x / 2, pos.x - size.x / 2, pos.y + size.x / 2);
        }
    }
}