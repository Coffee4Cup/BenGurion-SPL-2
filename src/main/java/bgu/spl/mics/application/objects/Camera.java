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
    private int numOfDetectedObjects;

    private LinkedList<StampedDetectedObjects> stampedDetectedObjects;

    public Camera(int id, int frequency, STATUS status, LinkedList<StampedDetectedObjects> stampedDetectedObjects) {
        this.id = id;
        this.frequency = frequency;
        this.status = status;
        numOfDetectedObjects = 0;
        this.stampedDetectedObjects = stampedDetectedObjects;
    }

    public StampedDetectedObjects  getStampedDetectedObjects(int time) {
        return stampedDetectedObjects.get(time);
    }


    public void objectDetected(){
        numOfDetectedObjects++;
    }

    public int getNumOfDetectedObjects() {
        return numOfDetectedObjects;
    }




    public int getId() {return id;}
    public int getFrequency() {return frequency;}
    public STATUS getStatus() {return status;}

    public void setStatus(STATUS status) {this.status = status;}
    public void setFrequency(int frequency) {this.frequency = frequency;}
    public void setId(int id) {this.id = id;}

    public String toString(){
        return "ID: " + id + " Frequency: " + frequency + " Status: " + status + " Detected Objects: " + stampedDetectedObjects;
    }

}
