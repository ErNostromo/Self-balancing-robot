
String inputString = "";
String inputStringToBl = "";
char inChar = '\0';
bool stringComplete = false;

void setup() {
  Serial.begin(115200);
  Serial1.begin(115200);
}

void loop() {

  if (Serial1.available() > 0) {
    while (Serial1.available() > 0 && inChar != ';') {
      inChar = Serial1.read();
      if (inChar != ';') {
        inputString += inChar;
        stringComplete = false;
      }
      else stringComplete = true;
    }
    if (stringComplete) {
      Serial.print("Dati da Bluetooth: ");
      Serial.println(inputString);
      //Serial1.println(inputString);
      //if (inputString=="\1\n") Serial1.println("eh si");
      inputString = "";
      inChar = '\0';
      stringComplete = false;
    }
  }

  if (Serial.available() > 0) {
    while (Serial.available() > 0 && inChar != ';') {
      inChar = Serial.read();
      if (inChar != ';') {
        inputString += inChar;
      }
    }
    Serial.print("Ricevuto: ");
    Serial.println(inputString);
    Serial1.println(inputString);
    inputString = "";
    inChar = '\0';
  }
}

