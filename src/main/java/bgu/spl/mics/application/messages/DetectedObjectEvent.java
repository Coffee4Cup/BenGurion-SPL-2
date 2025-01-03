package bgu.spl.mics.application.messages;
import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.application.objects.DetectedObject;
import bgu.spl.mics.application.objects.StampedDetectedObjects;

import java.util.LinkedList;

public class DetectedObjectEvent implements Event<Boolean> {

    private Future<Boolean> answer;
    private int amount;
    private StampedDetectedObjects detectedObjects;

    public DetectedObjectEvent(StampedDetectedObjects detectedObjects) {
        answer = new Future<>();
        amount = 1;
        this.detectedObjects = detectedObjects;
    }

    public int getAmount(){
        return amount;
    }

    public void setAmount(int newAmount){
        amount = newAmount;
    }

    @Override
    public Future<Boolean> getFuture() {
        return answer;
    }

    public StampedDetectedObjects getStampedDetectedObjects() {
        return detectedObjects;
    }

    @Override
    public String toString() {
        return "\'Detecting Objects\': " +"Object amount: " + amount + ", " + "detected Objects: " + detectedObjects;
    }
}
