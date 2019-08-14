class Button {
  final int COLOR_STATE0 = color(200);
  final int COLOR_STATE1 = color(150);

  int buttonX, buttonY, buttonWidth, buttonHeight;
  boolean overButton, buttonOn;
  String txt;
  Button(int tempbuttonX, int tempbuttonY, int tempbuttonWidth, int tempbuttonHeight, String temptxt) {
    buttonX = tempbuttonX;
    buttonY = tempbuttonY;
    buttonWidth = tempbuttonWidth;
    buttonHeight = tempbuttonHeight;
    txt = temptxt;
  }

  void buttonDisplay() {
    buttonOn = mousePressed && isOver(mouseX, mouseY);
    if (!buttonOn)
      fill(COLOR_STATE0);
    else
      fill(COLOR_STATE1);
    stroke(0);
    if (isOver(mouseX, mouseY)) {
      strokeWeight(5);
    } else {
      strokeWeight(3);
    }
    rect(buttonX, buttonY, buttonWidth, buttonHeight);
    textAlign(CENTER, CENTER);
    fill(0);
    textSize(50);
    text(txt, buttonX+buttonWidth/2, buttonY+buttonHeight/2);
  }

  boolean isOver(float x, float y) {
    return x > buttonX && x < buttonX+buttonWidth && y > buttonY && y < buttonY+buttonHeight;
  }
  /*
  boolean hasClicked() {
   boolean changeState = isOver(mouseX, mouseY);
   if (changeState) {
   buttonOn = !buttonOn;
   }
   return changeState;
   }
   */
}