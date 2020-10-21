#ifdef _WIN32
#include <Windows.h>
#else
#include <unistd.h>
#endif

#include <iostream>
#include <Pushbutton.h>

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
const int g_led_status=0;


void init()
{
#ifndef NO_PI
    wiringPiSetup();
    pinMode(LedColor, OUTPUT);
    pinMode(ButtonPin, INPUT);
    pullUpDnControl(ButtonPin, PUD_UP);    
#endif
}


int main()
{
    init();
    
    while(1)
    {
        if(0 == digitalRead(ButtonPin))
        {
            g_led_status =! g_led_status;
        }
        digitalWrite(LedColor,g_led_status);
    }
    return 0;
}




