package remote.testers;

import processing.core.PApplet;
import remote.Slider;

public class SliderTester extends PApplet {
    Slider s1;

    public static void main(String[] args) {
        PApplet.main("remote.SliderTest");
    }

    public void settings() {
        size(600, 600);
    }

    public void setup() {
        s1 = new Slider(this);
    }

    public void draw() {
        background(127);

        s1.update();
        System.out.print("\r" + s1.getValue());

        drawSlider(s1);
    }

    private void drawSlider(Slider s) {
        rectMode(CENTER);
        fill(s.sliderColor);
        stroke(0);
        strokeWeight(1);
        rect(s.sliderPos.x, s.sliderPos.y, s.sliderSize.x, s.sliderSize.y);
        fill(s.isFollowing() ? s.cursorActivatedColor : s.cursorDefaultColor);
        rect(s.cursorAbsolutePos.x, s.cursorAbsolutePos.y, s.cursorSize.x, s.cursorSize.y);
    }
}