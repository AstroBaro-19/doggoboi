/*
  Lezione 20: Pulsante come Interruttore
  Accende un LED mediante pulsante.  
  Creato 16 Mar 2020
  da Andrea Primavera
*/
 
int pinLed       = 2;
int pinButton    = 11;
int valButtonOld = LOW; // Memorizza lo stato passato del pulsante (evita letture multiple)
int ledState     = LOW; // Memorizza lo stato del led (Acceso o spento)
 
void setup() {
  pinMode(pinLed, OUTPUT);
  pinMode(pinButton, INPUT);
}
 
void loop() {
  int valButton = digitalRead(pinButton);
  
  if(valButton==HIGH && valButtonOld==LOW)
    ledState = !ledState; // Inverte lo stato (da ON a OFF, da OFF a ON) 
  
  digitalWrite(pinLed, ledState);
  valButtonOld = valButton;
}
