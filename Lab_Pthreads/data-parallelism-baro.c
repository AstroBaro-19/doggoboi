#include <stdio.h>
#include <pthread.h>

#define NUM_THREADS 4
#define N_ELEMENTS 20

int arr[N_ELEMENTS]; //creazione array di N_ELEMENTS


void * pthreads_fn(void * args)
{
    int id_thread = (int)args; // This is my uniqe identifier, as assigned by programmer in main()
    unsigned int chunk=N_ELEMENTS/NUM_THREADS; //quante iterazioni ogni thread esegue

    unsigned int istart=id_thread*chunk;
    unsigned int iend=istart+chunk;


    printf("Hello i'm Thread #%d, Chunk %u, istart %u, iend %u \n",id_thread,chunk,istart,iend);

    for(int i=istart;i<iend;i++)
    {
        printf("writing element at %d\n",i);
        arr[i]=i*2;
    }
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
