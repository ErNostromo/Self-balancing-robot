package remote.testers;

import processing.core.PApplet;
import remote.Button;

public class ButtonTester extends PApplet {
    private Button b1, b2;

    public static void main(String[] args) {
        PApplet.main("remote.testers.ButtonTester");
    }

    public void settings() {
        size(600, 600);
    }

    public void setup() {
        b1 = new Button(this);
        b1.setName("Pulsante");
        b1.textSize = 30;
        b1.adjustSizeToFitName();

        b2 = new Button(this, width / 2, 100, "swag", 18);
    }

    public void draw() {
        background(127);
        b1.update();
        b2.update();
        b1.updateDraw();
        b2.updateDraw();
    }
}