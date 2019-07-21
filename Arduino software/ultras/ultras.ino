#define triggerPin 7
#define echoPin 8
unsigned long time_now;

void setup() {
  pinMode(triggerPin, OUTPUT);
  pinMode(echoPin, INPUT);
  Serial.begin(9600);
  Serial.println("Sensore ultrasuoni: ");
  digitalWrite(triggerPin, LOW);
}

void loop() {
  time_now = micros();
  digitalWrite(triggerPin, HIGH);
  delayMicroseconds(10);
  digitalWrite(triggerPin, LOW);
  long durata = pulseIn(echoPin, HIGH);
  long distanza= 0.034*durata/2;
  /*
  Serial.print ("Distanza: ");
  if (durata>38000) {
    Serial.println("fuori portata");
  }
  else {
    Serial.print(distanza);
    Serial.println (" cm");
  }
  */
  Serial.println(micros()-time_now);
  delay(100);
}

