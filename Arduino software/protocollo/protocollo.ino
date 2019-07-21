short Stato_ser = 0;
short dato, preamb1, preamb2, tipo, dato_recv, chk_recv, chk_calc;
short end_recv_succesful = 0;

void setup() {
  // put your setup code here, to run once:
  Serial.begin(115200);
}

void loop() {
  // put your main code here, to run repeatedly:
  recv_serial();
}

void recv_serial() {
  if (Serial.available() > 0) {
    while (Serial.available() > 0) {
      dato = Serial.read();
      switch (Stato_ser) {
        case 0:     //preambolo 1
          if (dato == 5) {
            Stato_ser = 1;
            preamb1 = dato;
          }
          else {
            Serial.println("Preambolo scorretto. Torno in ascolto.");
            Stato_ser = 0;
          }
          break;
        case 1:     //preambolo 2
          if (dato == 5) {
            Stato_ser = 2;
            preamb2 = dato;
          }
          else {
            Serial.println("Preambolo scorretto. Torno in ascolto.");
            Stato_ser = 0;
          }
          break;
        case 2:
          tipo = dato;
          switch (dato) {
            case 'p': //picamera
              Serial.println("picamera");
              Stato_ser = 3;
              break;

            case 'm': //movimento
              Serial.println("movimento");
              Stato_ser = 3;
              break;

            case 's': //sensore
              Serial.println("sensore");
              Stato_ser = 3;
              break;

            case 'c': //comando
              Serial.println("comando");
              Stato_ser = 3;
              break;

            default:
              Serial.println("Tipo scorretto. Torno in ascolto.");
              Stato_ser = 0;
          }
          break;
        case 3:
          dato_recv = dato;
          switch (tipo) {
            case 'p':
              if (dato == 1) {
                Serial.println("Destra");
              }
              else if (dato == 2) {
                Serial.println("Sinistra");
              }
              break;

            case 'm':
              if (dato & 0b1) {
                Serial.println("Avanti");
              }
              else if (dato & 0b10) {
                Serial.println("Indietro");
              }
              if (dato &0b100) {
                Serial.println("Destra");
              }
              else if (dato & 0b1000) {
                Serial.println("Sinistra");
              }
              break;

            case 's':
              if (dato == 1) {
                Serial.println("Ostacolo in lontantanza");
              }
              else if (dato == 2) {
                Serial.println("Ostacolo vicino");
              }
              break;

            case 'c':
              if (dato == 0) {
                Serial.println("Disable sign");
              }
              else if (dato == 1) {
                Serial.println("Enable sign");
              }
              break;
          }
          Stato_ser = 4;
          break;
        case 4:     //checksum
          chk_recv = dato;
          chk_calc = preamb1 + preamb2 + tipo + dato_recv;
          if (chk_recv == chk_calc) {
            //acquisizione corretta
            Serial.println("Acquisizione corretta");
            Serial.print("Dato (");
            Serial.print((char)tipo);
            Serial.print("): ");
            Serial.println(dato_recv);
          }
          else {
            //acquisizione errata
            Serial.print("Acquisizione errata: chk_recv: ");
            Serial.print(chk_recv);
            Serial.print("; chk_calc: ");
            Serial.println(chk_calc);
          }
          Stato_ser = 0;
          end_recv_succesful = 1;
          break;
      }
    }
    Stato_ser = 0;  //commenta per analizzare anche in più volte
    if (!end_recv_succesful) {
      Serial.println("Trasmissione non finita");
    }
    else {
      Serial.println("OK");
      end_recv_succesful = 0;
    }
  }
}



