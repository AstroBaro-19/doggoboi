#ifdef _WIN32
#include <Windows.h>
#else
#include <unistd.h>
#endif

#include <iostream>

using namespace std;

//configurazione
const int Red=0;
const int Green=1;
const int Yellow=2;



//funzione setLed
void setLed(int LedColor, bool value)
{
    cout << "Setting Led " << LedColor << " to " << (value ? "ON" : "OFF") << endl;    
}

//blink Yellow inizializzazione semaforo
void blinkYellow()
{  
    bool onoff=true;
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
void setOnOff(int time, int led)
{
    setLed(led,true);
    sleep(time);
    setLed(led,false);
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
    blinkYellow();
    normalCycle();
    return 0;
}