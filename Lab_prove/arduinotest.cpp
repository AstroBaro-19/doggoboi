/*
  Lezione 20: Pulsante come Interruttore
  Accende un LED mediante pulsante.  
  Creato 16 Mar 2020
  da Andrea Primavera
*/
 
#ifdef _WIN32
#include <Windows.h>
#else
#include <unistd.h>
#endif

#include <iostream>

#ifndef NO_PI 
#include <wiringPi.h>
#endif 

using namespace std;
 
const int pinLed = 0;
const int pinButton = 1;
bool valButtonOld = false; // Memorizza lo stato passato del pulsante (evita letture multiple)
bool ledState = true; // Memorizza lo stato del led (Acceso o spento)
 
void init() 
{
#ifndef NO_PI
  wiringPiSetup();
  pinMode(pinLed, OUTPUT);
  pinMode(pinButton, INPUT);
#endif
}

/*
void setLed(int ledNumber, bool value)
{
#ifndef NO_PI
    digitalWrite(ledNumber, value);
#else
    cout << "Setting led " << ledNumber << " to " << (value ? "ON" : "OFF") << endl;
#endif
*/

void cycle()
{
    while(1)
    {
    
      #ifndef NO_PI
        int valButton = digitalRead(pinButton);
        
        if(valButton==true and valButtonOld==false)
        {
          ledState = !ledState; // Inverte lo stato (da ON a OFF, da OFF a ON) 
          cout << "Led: " << ledState << endl;
          cout << "read button X: " << valButton << endl;
          break;
        }
        else
        {
          digitalWrite(pinLed, ledState);
          cout << "Led: " << ledState << endl;
          cout << "read button Y: " << valButton << endl;
          valButtonOld = valButton;
        }
      #endif
    }
}


int main() 
{

  init();
  cycle();

  return 0;
}
