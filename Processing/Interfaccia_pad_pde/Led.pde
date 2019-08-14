class Led {
  int lxpos, lypos, ldiam;
  String ltxt;
  color lOn, lOff;
  Led (int templxpos, int templypos, int templdiam, String templtxt, color templOn, color templOff) {
    lxpos = templxpos;
    lypos = templypos;
    ldiam = templdiam;
    ltxt = templtxt;
    lOn = templOn;
    lOff = templOff;
  }

  void Display (boolean lTrigger) {
    fill(0);
    stroke(0);
    strokeWeight(2);
    textSize(30);
    textAlign(CENTER, CENTER);
    text(ltxt, lxpos, lypos-ldiam/2-50);
    strokeWeight(3);
    if (lTrigger) fill (lOn);
    else fill(lOff);
    ellipseMode(CENTER);
    ellipse(lxpos, lypos, ldiam, ldiam);
  }
}