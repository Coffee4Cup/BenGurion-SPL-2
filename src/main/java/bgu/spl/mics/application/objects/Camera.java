package bgu.spl.mics.application.objects;
import bgu.spl.mics.application.GurionRockRunner;
import bgu.spl.mics.application.messages.DetectedObjectEvent;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Represents a camera sensor on the robot.
 * Responsible for detecting objects in the environment.
 */
public class Camera {
    private int id;
    private int frequency;
    private STATUS status;
    private final Object lock;
    private final StatisticalFolder statisticalFolder;

    private HashMap<Integer, StampedDetectedObjects> detectedObjectList; //might get changed

    public Camera(int id, int frequency, STATUS status, LinkedList<StampedDetectedObjects> stampedDetectedObjects, StatisticalFolder statisticalFolder) {
        this.id = id;
        this.frequency = frequency;
        this.status = status;
        lock = new Object();
        detectedObjectList = new HashMap<>();
        for(StampedDetectedObjects sto: stampedDetectedObjects) {
            detectedObjectList.put(sto.getTime(), sto);
        }
        this.statisticalFolder = statisticalFolder;
    }

    public StampedDetectedObjects  getDetectedObjectList(int time) {
        return detectedObjectList.get(time);
    }


    public void objecstDetected(int amount){
        synchronized (lock){
            statisticalFolder.addDetectedObjects(amount);
        }
    }


    public int getId() {return id;}
    public int getFrequency() {return frequency;}
    public STATUS getStatus() {return status;}

    public void setStatus(STATUS status) {this.status = status;}
    public void setFrequency(int frequency) {this.frequency = frequency;}
    public void setId(int id) {this.id = id;}

    public String toString(){
        return "ID: " + id + " Frequency: " + frequency + " Status: " + status + " Detected Objects: " + detectedObjectList;
    }

}
