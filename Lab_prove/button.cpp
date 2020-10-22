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

int pinLed       = 0;
int pinButton    = 1;
 
void setup() {
#ifndef NO_PI
  wiringPiSetup();
  pinMode(pinLed, OUTPUT);
  pinMode(pinButton, INPUT);
#endif
}

void cycle()
{
#ifndef NO_PI
  int valButton = digitalRead(pinButton);
  
  while(1)
  {
    if(valButton==HIGH)
    {
      digitalWrite(pinLed,valButton);
    }
    else
    {
      digitalWrite(pinLed, valButton);
    }
    
  }
#endif
}


int main() {
  setup();
  cycle();
}
