

#ifdef _WIN32
#include <Windows.h>
#else
#include <unistd.h>
#endif

#include <iostream>

using namespace std;

//configurazione
const int Led=0;
const int timeout=1000;


//inizializzazione funzioni
void setLed(int ledNumber, bool value)
{
    cout << "Setting led " << ledNumber << " to " << (value ? "ON" : "OFF") << endl;
}

//main program
int main()
{
    bool onoff=true;

    while(1)
    {

        setLed(Led,onoff);
        onoff=!onoff;

//        delay(timeout);
        usleep(timeout * 1000);
    } // main loop

    return 0;
}