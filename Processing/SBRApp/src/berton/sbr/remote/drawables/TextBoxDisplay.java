package berton.sbr.remote.drawables;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;

import java.util.Iterator;
import java.util.LinkedList;

public class TextBoxDisplay implements Drawable {
    private PApplet p;
    private LinkedList<String> lines;
    private Iterator iterator;
    private int maxLines;
    private int textSize;

    public PVector pos, size;
    public int backgroundColor;

    public void setup(PApplet main, float x, float y, float w, float h) {
        p = main;
        pos = new PVector(x, y);
        size = new PVector(w, h);
        lines = new LinkedList<String>();
        backgroundColor = 255;
        setMaxLines(10);
    }

    public TextBoxDisplay(PApplet main) {
        setup(main, main.width / 20, main.height / 20, 18 * main.width / 20, 18 * main.height / 20);
    }

    public TextBoxDisplay(PApplet main, float x, float y, float w, float h) {
        setup(main, x, y, w, h);
    }

    public void setMaxLines(int max) {
        maxLines = max;
        textSize = (int) size.y / maxLines;
    }

    public void setTextSize(int size) {
        textSize = size;
        maxLines = (int) this.size.y / textSize;
    }

    public void insertLine(String text) {
        lines.add(text);
        if (lines.size() > maxLines) {
            lines.removeFirst();
        }
    }

    public void clear() {
        lines = new LinkedList<String>();
    }

    @Override
    public void update() {

    }

    @Override
    public void updateDraw() {
        p.fill(backgroundColor);
        p.rectMode(PConstants.CORNER);
        p.stroke(0);
        p.strokeWeight(0);
        p.rect(pos.x, pos.y, size.x, size.y);
        p.textAlign(PConstants.LEFT, PConstants.TOP);
        p.textSize(textSize);
        p.fill(0);
        iterator = lines.iterator();
        int size = lines.size();
        int count = 0;
        while (iterator.hasNext()) {
            String text = (String) iterator.next();
            float y = pos.y + this.size.y / maxLines * (maxLines - size + count);
            float x = pos.x + 0;
            p.text(text, x, y);
            count++;
        }
    }

    public int getNumberOfLines() {
        return lines.size();
    }
}