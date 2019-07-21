char tipo = 'm', dato = 4, chk;

void setup() {
  // put your setup code here, to run once:
  Serial.begin (115200);
  
}

void loop() {
dato=0;
for (int i=0; i<15; i++) {
  chk = 5+5+tipo+dato;
  Serial.print (5);
  Serial.print (5);
  Serial.print (tipo);
  Serial.print (dato);
  Serial.print (chk);
  dato++;
  delay (1000);
}
}
