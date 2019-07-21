class Slider {
  float rectX, rectY;
  float sliderX, sliderY=0, sliderW, sliderH, minRange, maxRange;
  String name;
  boolean premuto = false;
  Slider (float tempCx, float tempCy, float tempWidth, float tempHeight, float tempMin, float tempMax, float tempDefault, String tempName) {
    sliderX = tempCx;
    sliderY = tempCy;
    sliderW = tempWidth;
    sliderH = tempHeight;
    rectX = map(tempDefault, tempMin, tempMax, tempCx-tempWidth/2+tempHeight/2, tempCx+tempWidth/2-tempHeight/2);
    rectY = sliderY;
    minRange = tempMin;
    maxRange = tempMax;
    name = tempName;
  }

  void display() {
    fill(255);
    rectMode(CENTER);
    rect (sliderX, sliderY, sliderW, sliderH);
    if (mousePressed && isOver(mouseX, mouseY)) premuto = true;
    if (premuto) {
      rectX = mouseX;
      if (rectX < (sliderX-sliderW/2+sliderH/2)) rectX = sliderX-sliderW/2+sliderH/2;
      else if (rectX > (sliderX + sliderW/2-sliderH/2)) rectX = sliderX + sliderW/2-sliderH/2;
    }
    fill(200);
    rect(rectX, rectY, sliderH, sliderH);
    fill(0);
    textSize(50);
    textAlign(LEFT, BOTTOM);
    text (name, sliderX-sliderW/2, sliderY-sliderH/2-20);
    textAlign(LEFT, CENTER);
    text (getValue(), sliderX+sliderW/2+100, sliderY);
  }

  float getValue() {
    float rawValue = rectX-(sliderX-sliderW/2);
    return map(rawValue, sliderH/2, sliderW-sliderH/2, minRange, maxRange);
  }

  boolean isOver(float x, float y) {
    return x > (sliderX-sliderW/2) && x < (sliderX+sliderW/2) && y > (sliderY-sliderH/2) && y < (sliderY + sliderH/2);
  }
}