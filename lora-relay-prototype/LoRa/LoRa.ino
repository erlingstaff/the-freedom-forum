#include <SoftwareSerial.h>

SoftwareSerial myserial(2, 3); // RX, TX
String readString;

void setup() {
  myserial.begin(115200); //Initialize GSM serial port
  Serial.begin(115200); //Initialize Arduino default serial port
  delay(50);
  loraConfig();
}

void loraConfig() {
  myserial.println("AT+RESET");
  delay(50);
  myserial.println("AT+MODE=0");
  delay(50);
  myserial.println("AT+IPR=115200");
  delay(50);
  myserial.println("AT+PARAMETER=9,7,1,12");
  delay(50);
  myserial.println("AT+BAND=868500000");
  delay(50);
  myserial.println("AT+ADDRESS=5");
  delay(50);
  myserial.println("AT+NETWORKID=5");
  delay(50);
  myserial.println("AT+CRFOP=22");
}

void loop() {
  while (myserial.available()) {
    Serial.write(myserial.read());
  }
  while (Serial.available()) {
    myserial.write(Serial.read());
  }
}

void checkIfRelayMessage(String message) {
  if (message.indexOf("+RECV") > 0) {
    checkMessage(message);
  }
}

void checkMessage(String message) {
  if (message.length() < 240) {
    sendMessageOverLoRa(message);
    sendMessageOverBLE(message);
  }
}

void sendMessageOverLoRa(String message) {
  String len = String(message.length());
  myserial.println("AT+SEND=5," + len + "," + message);
}

void sendMessageOverBLE(String message) {
  delay(5);
}
