package bgu.spl.mics.application.objects;
import bgu.spl.mics.application.GurionRockRunner;
import bgu.spl.mics.application.messages.DetectedObjectEvent;
import bgu.spl.mics.application.services.CameraService;

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
    private int finalTick;
    private boolean isDone;
    private final StatisticalFolder statisticalFolder;
    private CameraService service;
    private LinkedList<DetectedObject> lastFrames;

    private HashMap<Integer, StampedDetectedObjects> detectedObjectList; //might get changed

    public Camera(int id, int frequency, LinkedList<StampedDetectedObjects> stampedDetectedObjects, StatisticalFolder statisticalFolder) {
        this.id = id;
        this.frequency = frequency;
        this.status = STATUS.DOWN;
        lock = new Object();
        isDone = false;
        finalTick = 0;
        detectedObjectList = new HashMap<>();
        for(StampedDetectedObjects sto: stampedDetectedObjects) {
            if(sto.getTime() > finalTick){
                finalTick = sto.getTime();
            }
            detectedObjectList.put(sto.getTime(), sto);
        }
        this.statisticalFolder = statisticalFolder;
    }

    public void setService(CameraService service){
        this.service = service;
    }

    public void error(String description){
        setStatus(STATUS.ERROR);
        statisticalFolder.setErrorDescription(description);
        updateLastFrames();
    }

    public boolean isDone(){
        return isDone;
    }



    public StampedDetectedObjects  getDetectedObjectList(int time) {
        StampedDetectedObjects sdo;
        if(time >= finalTick){
            isDone = true;
            status = STATUS.DOWN;
        }
        if(detectedObjectList.containsKey(time - frequency)) {
            sdo =  detectedObjectList.remove(time - frequency);
            for(DetectedObject detectedObject: sdo.getDetectedObjects()) {
                if(detectedObject.id() == "error") {
                    error(detectedObject.description());
                    return null;
                }
            }
            objecstDetected(sdo.getNumOfDetectedObjects());
            lastFrames = sdo.getDetectedObjects();
            return sdo;
        }
        return null;
    }

    public void updateLastFrames(){
        statisticalFolder.setCameraLastFrames(lastFrames);
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
