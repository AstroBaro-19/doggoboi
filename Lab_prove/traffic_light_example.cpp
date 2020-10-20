
#ifdef _WIN32
#include <Windows.h>
#else
#include <unistd.h>
#endif

#include <iostream>

using namespace std;

//configurazione
const int timeout=1000;
const int Red=0;
const int Green=1;
const int Yellow=2;

//inizializzazione funzioni
void setLed(int LedColor, bool value)
{
    cout << "Setting Led " << Yellow << " to " << (value ? "ON" : "OFF") << endl;
        
}

void blinkYellow()
{
    bool onoff=true;
    int count=0;
    while(count<10)
    {
     setLed(Yellow,onoff);
     onoff=!onoff;   
     usleep(timeout*1000);
     count++;
    }
}

//main program
int main()
{
    blinkYellow();
}