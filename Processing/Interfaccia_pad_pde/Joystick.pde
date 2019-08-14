class Joystick {
  float jheight_big, jwidth_small, jwidth_big, jheight_small, jxpos, jypos;
  float jxpos_small=0, jypos_small=0;
  Joystick(float tempxpos, float tempypos, float tempwidth_big, float tempwidth_small) {
    jxpos = tempxpos;
    jypos = tempypos;
    jwidth_big = tempwidth_big;
    jheight_big = tempwidth_big;
    jwidth_small = tempwidth_small;
    jheight_small = tempwidth_small;
  }
  void Display() {
    fill(255);
    strokeWeight(2);
    ellipse(jxpos, jypos, jwidth_big, jheight_big);
    if (isOver(mouseX, mouseY)&& mousePressed) {  //SIAMO DENTRO
      fill(150);
      jxpos_small = mouseX;
      jypos_small = mouseY;
    } else {
      fill(200);
      jxpos_small = jxpos;
      jypos_small = jypos;
    }
    ellipse(jxpos_small, jypos_small, jwidth_small, jheight_small);
  }

  boolean isOver(float x, float y) {
    return dist(x, y, jxpos, jypos)<(jwidth_big/2);
  }

  float getX() {
    return jxpos_small-jxpos;
  }
  float getY() {
    return jypos-jypos_small;
  }
}