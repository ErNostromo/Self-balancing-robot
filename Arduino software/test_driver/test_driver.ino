/*
  #define step1 15
  #define dir1 14
  #define step2 16
  #define dir2 10
*/
#define step1 15
#define dir1 14
#define step2 16
#define dir2 10

int t = 500;  //us
int i = 0;
String inputString = "";
int state;

const int tmin = 300, tmax = 2000;

void setup() {
  // put your setup code here, to run once:
  /*
    pinMode(enable , OUTPUT);
    pinMode(m0 , OUTPUT);
    pinMode(m1 , OUTPUT);
    pinMode(m2 , OUTPUT);
    pinMode(reset , OUTPUT);
    pinMode(sleep , OUTPUT);
    pinMode(Step , OUTPUT);
    pinMode(dir , OUTPUT);
    digitalWrite(enable , LOW);
    digitalWrite(reset , LOW);
    digitalWrite(sleep , LOW);
    digitalWrite(m0 , LOW);
    digitalWrite(m1 , LOW);
    digitalWrite(m2 , LOW);
  */
  pinMode (step1, OUTPUT);
  pinMode (dir1, OUTPUT);
  digitalWrite (step1, LOW);
  digitalWrite(dir1, LOW);

  pinMode (step2, OUTPUT);
  pinMode (dir2, OUTPUT);
  digitalWrite (step2, LOW);
  digitalWrite (dir2, LOW);

  Serial.begin(115200);

  //while (!Serial) {}

  Serial.println("Inizio direzione positiva (2 sec...");
  long now = millis();
  while (millis() - now < 2000) {
    digitalWrite(step1, LOW);
    digitalWrite(step2, LOW);
    delayMicroseconds(t);
    digitalWrite(step1, HIGH);
    digitalWrite(step2, HIGH);
    delayMicroseconds(t);
  }
  Serial.println("Pausa (1 sec...)");
  delay(1000);
  Serial.println("Inizio direzione negativa (2 sec...)");
  digitalWrite(dir1, HIGH);
  digitalWrite(dir2, HIGH);
  now = millis();
  while (millis() - now < 2000) {
    digitalWrite(step1, LOW);
    digitalWrite(step2, LOW);
    delayMicroseconds(t);
    digitalWrite(step1, HIGH);
    digitalWrite(step2, HIGH);
    delayMicroseconds(t);
  }
  Serial.println("Fine");
  t = tmax;
}

void loop() {
  switch (state) {
    case 0:
      if (t > tmin) t--;
      else state = 1;
      break;

    case 1:
      if (t < tmax) t++;
      else state = 0;
      break;

    default:
      state = 0;
  }
  digitalWrite(step1, LOW);
  digitalWrite(step2, LOW);
  delayMicroseconds(t);
  digitalWrite(step1, HIGH);
  digitalWrite(step2, HIGH);
  delayMicroseconds(t);
  Serial.println(t);
}
