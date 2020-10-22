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
const int Red=0;
const int Yellow=1;
const int Green=2;
bool onoff=false;  

void init()
{
#ifndef NO_PI
    wiringPiSetup();
    pinMode(Red, OUTPUT); 
    pinMode(Green, OUTPUT); 
    pinMode(Yellow, OUTPUT); 
#endif
}

//funzione setLed
void setLed(int LedColor, bool value)
{
#ifndef NO_PI
    digitalWrite(LedColor,value);
#else    
    cout << "Setting Led " << LedColor << " to " << (value ? "ON" : "OFF") << endl;    
#endif
}

//blink Yellow 
//inizializzazione semaforo led spenti
void blinkYellow()
{      
    setLed(Red,onoff);
    setLed(Yellow,onoff);
    setLed(Green,onoff);
    
    onoff=!onoff;
    
    int count=0;
    while(count<10)
    {
    setLed(Yellow,onoff);
    onoff=!onoff;   
    sleep(1);
    count++;
    }
}

//settaggio tempo mantenimento luce
void setOnOff(int time, int Led)
{
    setLed(Led,true);
    sleep(time);
    setLed(Led,false);
}

//ciclo normale del semaforo
void normalCycle()
{
    int currentState=0;
    int timeR=15;
    int timeG=10;
    int timeY=5;

    while(1)
    {
        cout << "currentState: " << currentState << endl;
    
    
    if(currentState == 0){
            setOnOff(timeR,Red);
            currentState = 2;
        } else if(currentState == 2){
            setOnOff(timeG,Green);
            currentState = 1;
        } else{
            setOnOff(timeY,Yellow);
            currentState = 0;
        }    
        
    }
}

//main program
int main()
{
    init();
    blinkYellow();
    normalCycle();
    return 0;
}
