package bgu.spl.mics.application.objects;


public class EventRoundRobin {
    private int currentSize;
    private int index;

    public EventRoundRobin(int size){
        this.currentSize = size;
        index = 0;
    }

    public int indexIncrement(){
        return (index++)%currentSize;
    }

    public void sizeIncrement() {
        this.currentSize++;
    }

    /**
     * Getters used ONLY for tests
     */
    public final int getCurrentSize() {
        return currentSize;
    }
    public final int getIndex() {
        return index;
    }

}
