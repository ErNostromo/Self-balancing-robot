class checkBox {
  float x, y;
  float cwidth = 100, cheight = 100;
  int cstato = 0 ;
  boolean enable = false;
  checkBox(float tempcx, float tempcy) {
    x = tempcx;
    y = tempcy;
  }

  void display() {
    rectMode (CORNER);
    fill(255);
    rect (x, y, cwidth, cheight);
    fill(0);
    stroke(0);
    strokeWeight(2);
    switch (cstato) {
    case 0:
      enable = false;
      if (mousePressed && isOver(mouseX, mouseY)) cstato = 1;
      break;

    case 1:
      enable = true;
      if (!mousePressed && isOver(mouseX, mouseY)) cstato = 2;
      break;

    case 2:
      enable = true;
      if (mousePressed && isOver(mouseX, mouseY)) cstato = 3;
      break;
    case 3:
      enable = false;
      if (!mousePressed && isOver(mouseX, mouseY)) cstato = 0;
      break;
    }
    if (enable) {
      line(x, y, x+cwidth, y+cheight);
      line(x+cwidth, y, x, y+cheight);
    }
  }
  
  boolean isOver(float tx, float ty) {
    return tx > x && tx < x + cwidth && ty > y  && ty < y + cheight;
  }
  
  boolean enabled() {
    return enable;
  }
}