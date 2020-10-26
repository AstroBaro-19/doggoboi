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

int pinButton= 0;

void setup() {
#ifndef NO_PI
  wiringPiSetup();
  pinMode(pinButton,INPUT);
  pullUpDnControl(pinButton, PUD_UP);
#endif
}


void cycle()
{

  while(1)
  {

    if (digitalRead(pinButton)==0)
    {
      cout << "Button pressed" << endl;
      sleep(1);
    }
  }
}  




int main() 
{
  setup();
  cycle();
  return 0;
}

