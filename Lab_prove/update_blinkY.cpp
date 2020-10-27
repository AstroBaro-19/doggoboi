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
int pinButton=3;
bool ButtonState=false;

bool onoff=false;  

void init()
{
#ifndef NO_PI
    wiringPiSetup();
    pinMode(Red, OUTPUT); 
    pinMode(Green, OUTPUT); 
    pinMode(Yellow, OUTPUT); 
    pinMode(pinButton,INPUT);
    pullUpDnControl(pinButton, PUD_UP);
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

void initLeds()
{
    setLed(Red,onoff);
    setLed(Yellow,onoff);
    setLed(Green,onoff);
}


//blink Yellow 
//inizializzazione semaforo led spenti
void blinkYellow()
{  
    initLeds();
        
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
    initLeds();
            
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
    
    
    int ButtonCounter=1;
    while(ButtonCounter<=2)
    {

        if (digitalRead(pinButton)==0)
        {
          cout << "Button pressed " << ButtonCounter << " times" << endl;
          if (ButtonState==false)
          { 
            ButtonState=!ButtonState;
            ButtonCounter++;
            sleep(1);
            
            normalCycle();
          } 
          else
          {
            ButtonState=false;
            ButtonCounter++;
            sleep(1);
          }
        }
    }
    
    return 0;
}
