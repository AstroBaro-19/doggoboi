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
    while(count<10)  //blink 10 sec: 5s ON e 5s OFF
    {
        setLed(Yellow,onoff);
        onoff=!onoff;   
        sleep(1);
        count++;
        cout << "Blink Yellow for: " << count << " seconds" << endl;
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
    blinkYellow();   //funzione ripresa
    
    int currentState=0;
    int timeR=15;
    int timeY=5;
    int timeG=10;

    int i=0;
    while(i<3)
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
            
            i++;
            cout << "N° giri completati: " << i << endl;    
            
        }
    
    }
}

//main program
int main()
{
    init();
    normalCycle();
    return 0;
}
