package bgu.spl.mics.application.objects;
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
    private String name;
    private STATUS status;
    private final Object lock;
    private int finalTick;
    private boolean isDone;
    private final StatisticalFolder statisticalFolder;
    private CameraService service;
    private StampedDetectedObjects lastFrames;

    private HashMap<Integer, StampedDetectedObjects> detectedObjectList; //might get changed

    public Camera(int id, int frequency, String name, LinkedList<StampedDetectedObjects> stampedDetectedObjects, StatisticalFolder statisticalFolder) {
        this.id = id;
        this.frequency = frequency;
        this.name = name;
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

    /**
     * @pre: None
     * @post: The CameraService instance associated with this Camera is set.
     */
    public void setService(CameraService service){
        this.service = service;
    }

    /**
     * @pre: None
     * @post: The Camera status is set to ERROR, an error description is stored in the StatisticalFolder,
     *        and the last frames are updated.
     */
    public void error(String description){
        setStatus(STATUS.ERROR);
        statisticalFolder.setErrorDescription(description, name);
        updateLastFrames();
    }

    /**
     * @pre: None
     * @post: Returns true if the Camera has completed processing, false otherwise.
     */
    public boolean isDone(){
        return isDone;
    }

    /**
     * @pre: None
     * @post: Returns a StampedDetectedObjects instance for the specified time and removes it
     *        from the detectedObjectList if available. Updates status in case of an error.
     */
    public StampedDetectedObjects getDetectedObjectList(int time) {
        StampedDetectedObjects sdo;
        if(time >= finalTick + frequency){
            isDone = true;
            status = STATUS.DOWN;
        }
        if(detectedObjectList.containsKey(time - frequency)) {
            sdo =  detectedObjectList.remove(time - frequency);
            for(DetectedObject detectedObject: sdo.getDetectedObjects()) {
                if(detectedObject.id().equals("ERROR")) {
                    if(!sdo.removeError()) {
                        lastFrames = sdo;
                        error(detectedObject.description());
                        objecstDetected(sdo.getNumOfDetectedObjects());
                        return sdo;

                    }
                    else {
                        error(detectedObject.description());
                        return null;
                    }
                }
            }
            objecstDetected(sdo.getNumOfDetectedObjects());
            lastFrames = sdo;
            return sdo;
        }
        return null;
    }

    /**
     * @pre: lastFrames should not be null.
     * @post: Updates the last frames of the StatisticalFolder for this Camera.
     */
    public void updateLastFrames(){
        statisticalFolder.setCameraLastFrames(lastFrames, name);
    }


    /**
     * @pre: amount >= 0
     * @post: Adds the detected object count to the StatisticalFolder.
     */
    public void objecstDetected(int amount){
        synchronized (lock){
            statisticalFolder.addDetectedObjects(amount);
        }
    }


    /**
     * @pre: None
     * @post: Returns the Camera's ID.
     */
    public int getId() {return id;}

    /**
     * @pre: None
     * @post: Returns the frequency of the Camera.
     */
    public int getFrequency() {return frequency;}

    /**
     * @pre: None
     * @post: Returns the current status of the Camera.
     */
    public STATUS getStatus() {return status;}

    /**
     * @pre: None
     * @post: Sets the Camera's status to the specified STATUS value.
     */
    public void setStatus(STATUS status) {this.status = status;}

    /**
     * @pre: frequency >= 0
     * @post: Sets the Camera's frequency to the specified value.
     */
    public void setFrequency(int frequency) {this.frequency = frequency;}

    /**
     * @pre: id >= 0
     * @post: Sets the Camera's ID to the specified value.
     */
    public void setId(int id) {this.id = id;}

    /**
     * @pre: None
     * @post: Returns a string representation of the Camera's attributes.
     */
    public String toString(){
        return "ID: " + id + " Frequency: " + frequency + " Status: " + status + " Detected Objects: " + detectedObjectList;
    }

    /**
     * @pre: None
     * @post: Returns the Camera's name.
     */
    public String getName() {
        return name;
    }
}
