package remote;

import java.util.Vector;

import processing.core.*;

/**
 * TabManager manages all the tabs to be included in the sketch. It also does the
 * update() and updateDraw() of any Drawable added to the current tab.
 */
public class TabManager implements Drawable {
    private Vector<Tab> tabs;
    private Tab[] dummy;
    private PApplet p;
    private float nextX;
    private int row;
    private InputDrawable activeDrawable;

    public int charDimension;
    public float height;
    public int activeTab;

    private class Tab {
        private String name;
        private boolean activated;

        public Vector<Drawable> drawables;
        public PVector pos, size;

        private void setup(PApplet main, float x, float y, float w, float h, String tabName) {
            p = main;
            pos = new PVector(x, y);
            size = new PVector(w, h);
            name = tabName;

            drawables = new Vector<Drawable>();
            activated = false;
        }

        public Tab(PApplet main, float x, float y, float w, float h, String tabName) {
            setup(main, x, y, w, h, tabName);
        }

        public boolean isOver(float x, float y) {
            return x > pos.x && x < pos.x + size.x && y > pos.y && y < pos.y + size.y;
        }
    }

    private void setup(PApplet main) {
        p = main;
        tabs = new Vector<Tab>();
        dummy = new Tab[] {};
        row = 0;
        charDimension = 16;
        nextX = 0;
        height = charDimension + charDimension / 3 * 2;
    }

    /**
     * Initialise the TabManager
     */
    public TabManager(PApplet main) {
        setup(main);
    }

    /**
     * Add a new tab. The width can be configured by setting the
     * charDimension variable, but you can set directly the height.
     * @param tabName the name of the new Tab
     */
    public void addTab(String tabName) {
        p.textSize(16);
        if (nextX + p.textWidth(tabName) + 20 > p.width) {
            row++;
            nextX = 0;
        }
        Tab t = new Tab(p, nextX, row * height, p.textWidth(tabName) + 20, height, tabName);
        tabs.add(t);
        if (tabs.size() <= 1)
            t.activated = true;
        System.out.println("Added tab at x " + t.pos.x + ", y " + t.pos.y + ", width " + t.size.x + ", height "
                + t.size.y + ", name " + t.name);
        nextX += t.size.x;
    }

    /**
     * Insert a new Drawable in the specified Tab.
     * @param d the Drawable
     * @param tab the index of the Tab
     */
    public void insertDrawable(Drawable d, int tab) {
        if (d == null || tab < 0 || tab >= tabs.size())
            return;
        tabs.elementAt(tab).drawables.add(d);
    }

    public Drawable[] getDrawables(int tab) {
        return tabs.elementAt(tab).drawables.toArray(new Drawable[0]);
    }

    /**
     * Get the currently active Tab and update all the Drawables included.
     */
    @Override
    public void update() {
        for (int currentTab = 0; currentTab < tabs.size(); currentTab++) {
            Tab t = tabs.elementAt(currentTab);
            if (currentTab != activeTab)
                t.activated = false;
            if (activeDrawable == null && p.mousePressed && t.isOver(p.mouseX, p.mouseY)) {
                t.activated = true;
                activeTab = currentTab;
            }

            if (activeTab == currentTab) {
                for (Drawable d : t.drawables) {
                    if (d instanceof InputDrawable) {
                        InputDrawable in = (InputDrawable) d;
                        if (activeDrawable == null) {
                            in.update();
                            if (in.isBeingUsed())
                                activeDrawable = in;
                        } else {
                            activeDrawable.update();
                            if (!p.mousePressed && !activeDrawable.isBeingUsed()) {
                                activeDrawable = null;
                            }
                        }
                    } else {
                        d.update();
                    }
                }
            }
        }
    }

    /**
     * Update the draws of all the Tabs, and the Drawables of the currently active one.
     */
    @Override
    public void updateDraw() {
        for (int i = 0; i < tabs.size(); i++) {
            Tab t = tabs.elementAt(i);
            p.rectMode(PConstants.CORNER);
            p.stroke(0);
            p.strokeWeight(0);
            p.textSize(charDimension);
            p.textAlign(PConstants.LEFT, PConstants.TOP);
            p.fill(t.activated ? 200 : 255);
            p.rect(t.pos.x, t.pos.y, t.size.x, t.size.y);
            p.fill(100);
            p.text(t.name, t.pos.x + 10, t.pos.y);
            if (i == activeTab)
                for (int j = 0; j < t.drawables.size(); j++) {
                    t.drawables.elementAt(j).updateDraw();
                }

            if (i >= tabs.size() - 1 || tabs.elementAt(i + 1).pos.y > t.pos.y) {
                p.fill(240);
                p.rectMode(PConstants.CORNERS);
                p.rect(t.pos.x + t.size.x, t.pos.y, p.width, t.pos.y + t.size.y);
            }
        }
    }
}