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
const int timeoutMs = 1000; // 1 second

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


int main()
{
    init();
    
    bool onoff = true;

    while(1)
    {
		if (digitalRead(ButtonPin)==LOW)
		{
			setLed(LedColor, onoff);
			onoff = !onoff;

	#ifndef NO_PI
			delay(timeoutMs);
	#else
			usleep(timeoutMs * 1000);
	#endif
		} // main loop

	}
        
    return 0;
}

/*
int main()
{
    init();
    
    bool onoff = true;

    while(1)
    {
		if (digitalRead(ButtonPin)==LOW)
		{
			setLed(LedColor, onoff);
			onoff = !onoff;

	#ifndef NO_PI
			delay(timeoutMs);
	#else
			usleep(timeoutMs * 1000);
	#endif
		} // main loop

	}
        
    return 0;
}
*/
