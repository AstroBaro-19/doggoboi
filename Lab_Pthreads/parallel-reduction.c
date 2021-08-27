#include <stdio.h>
#include <pthread.h>

#define NUM_THREADS 4
#define N_ELEMENTS 20

int arr[N_ELEMENTS];

void * pthreads_fn(void * args)  //funzione che un thread svolge
{
    unsigned int chunk=N_ELEMENTS/NUM_THREADS;
    unsigned int id_thread=(int) args;

    unsigned int istart=id_thread*chunk;
    unsigned int iend=istart+chunk;
    printf("Thread #%d, chunk %u, istart %u, iend %u\n",id_thread,chunk,istart,iend);
    int somma_parz=0;
    for(int i=istart;i<iend;i++)
    {
        arr[i]=i;
        somma_parz=somma_parz+i;
    }
    printf("Somma parziale thread #%d, somma %u\n",id_thread,somma_parz);
    return 0;
}


int main()
{
    pthread_t mythreads[NUM_THREADS];
    pthread_attr_t myattr;
    void *returnvalue;

    //inizializzazione array N_ELEMENTS
    for(int i=0;i<N_ELEMENTS;i++)
    {
        arr[i]=0;
    }

    for(int i=0; i<NUM_THREADS; i++) // ==> FORK
    {
        pthread_attr_init(&myattr);
        int err = pthread_create (&mythreads[i], &myattr, pthreads_fn, (void *) i); // Pass 'i' as identifier for thread
        pthread_attr_destroy(&myattr);
    }

    // Now, the man (master) thread can do other useful stuff, here
    // while other (slave) threads execute in parallel
    
    
    for(int i=0; i<NUM_THREADS; i++) // <== JOIN
        pthread_join(mythreads[i], returnvalue); // Now, returnvalue contains the value returned by pthreads_fn

    //elementi dell'array
    for(int i=0; i<N_ELEMENTS; i++)
        printf("arr[%d] is %d\n", i, arr[i]);

    return 0;
}