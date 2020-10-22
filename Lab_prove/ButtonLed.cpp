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

///
/// Configuration
///
const int LedColor= 0;
const int ButtonPin=1;
int ledState=0;

///
/// Utils
///
void init()
{
#ifndef NO_PI
    wiringPiSetup();
    pinMode(LedColor, OUTPUT);
    pinMode(ButtonPin, INPUT);
    
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

    while(1)
    {
		cout << "segnale input: " << digitalWrite(ButtonPin,value) << endl;
		sleep(1);
	}

}

int main()
{
    init();
    cycle();
            
    return 0;
}
