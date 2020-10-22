

#ifdef _WIN32
#include <Windows.h>
#else
#include <unistd.h>
#endif

#ifndef NO_PI
#include <wiringPi.h>
#endif

#include <iostream>

using namespace std;

//configurazione
const int Led=0;
const int timeout=1000;

void init()
{
#ifndef NO_PI
    wiringPiSetUp();
    pinMode(Led,OUTPUT);
#endif

}


//inizializzazione funzioni
void setLed(int ledNumber, bool value)
{
#ifndef NO_PI
    digitalWrite(Led,value);
#else
    cout << "Setting led " << ledNumber << " to " << (value ? "ON" : "OFF") << endl;
#endif
}

//main program
int main()
{
    init();
    
    bool onoff=true;

    while(1)
    {
        
        setLed(Led,onoff);
        onoff=!onoff;

#ifdef NO_PI
        delay(timeout);
#else
        usleep(timeout * 1000);
#endif
    } // main loop

    return 0;
}
