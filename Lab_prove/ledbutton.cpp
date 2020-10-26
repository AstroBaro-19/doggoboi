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

int pinButton=0;
int pinLed=2;
bool ButtonState=false;

void setup() {
#ifndef NO_PI
  wiringPiSetup();
  pinMode(pinLed,OUTPUT);
  pinMode(pinButton,INPUT);
  pullUpDnControl(pinButton, PUD_UP);
#endif
}


void setLed(int ledNumber, bool value)
{
#ifndef NO_PI
    digitalWrite(ledNumber, value);
#else
    cout << "Setting led " << ledNumber << " to " << (value ? "ON" : "OFF") << endl;
#endif
}


void cycle()
{
  setLed(pinLed,false); //inizializzo il led false per azzerare eventuali errori
  
  int ButtonCounter=1;
  while(ButtonCounter<=2)
  {

    if (digitalRead(pinButton)==0)
    {
      cout << "Button pressed " << ButtonCounter << " times" << endl;
      if (ButtonState==false)
      { 
        ButtonState=!ButtonState;
        setLed(pinLed,ButtonState);
        ButtonCounter++;
        sleep(1);
      }
      else
      {
        ButtonState=false;
        setLed(pinLed,ButtonState);
        ButtonCounter++;
        sleep(1);
      }
    }
  }
}  




int main() 
{
  setup();
  cycle();
  return 0;
}

