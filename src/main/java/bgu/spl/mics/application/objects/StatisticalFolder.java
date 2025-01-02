package bgu.spl.mics.application.objects;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Holds statistical information about the system's operation.
 * This class aggregates metrics such as the runtime of the system,
 * the number of objects detected and tracked, and the number of landmarks identified.
 */
public class StatisticalFolder {
    private final AtomicInteger systemRunTime;
    private final AtomicInteger numDetectedObjects;
    private final AtomicInteger numTrackedObjects;
    private final AtomicInteger numLandmarks;
    private LinkedList<DetectedObject> cameraLastFrames;
    private LinkedList<TrackedObject> lidarLastFrames;
    private String errorDescription;
    private LinkedList<Pose> lastPoses;

    private LinkedList<LandMark> landmarks;

    public StatisticalFolder(){
        systemRunTime = new AtomicInteger(0);
        numDetectedObjects = new AtomicInteger(0);
        numTrackedObjects = new AtomicInteger(0);
        numLandmarks = new AtomicInteger(0);
    }

    public void setErrorDescription(String errorDescription){
        this.errorDescription = errorDescription;
    }

    public void setSystemRunTime(Integer amount){
        int oldValue;
        int newValue;
        do{
            oldValue = this.systemRunTime.get();
            newValue = oldValue + amount;
        }while(!this.systemRunTime.compareAndSet(oldValue, newValue));
    }

    public void setFinalLandMarks(LinkedList<LandMark> landmarks){
        this.landmarks = landmarks;
    }

    public void addDetectedObjects(int amount){
        int oldValue;
        int newValue;
        do{
            oldValue = this.numDetectedObjects.get();
            newValue = oldValue + amount;
        }while(!this.numDetectedObjects.compareAndSet(oldValue, newValue));
    }

    public void addTrackedObjects(int amount){
        int oldValue;
        int newValue;
        do{
            oldValue = this.numTrackedObjects.get();
            newValue = oldValue + amount;
        }while(!this.numTrackedObjects.compareAndSet(oldValue, newValue));
    }

    public void addLandMarks(int amount){
        int oldValue;
        int newValue;
        do{
            oldValue = this.numLandmarks.get();
            newValue = oldValue + amount;
        }while(!this.numLandmarks.compareAndSet(oldValue, newValue));
    }

    public String toString(){
        String output = "SystemRunTime: "+systemRunTime.get()+
                " DetectedObjects: "+numDetectedObjects.get()+
                " TrackedObjects: "+numTrackedObjects.get()+
                " Landmarks: "+numLandmarks.get();
        output += "\nLandmarks: "+landmarks;
        return output;
    }

    public void setCameraLastFrames(LinkedList<DetectedObject> lastFrames) {
        this.cameraLastFrames = lastFrames;
    }

    public void setTrackedObjects(LinkedList<TrackedObject> lastTrackedObjects) {
        this.lidarLastFrames = lastTrackedObjects;
    }

    public void setLastPoses(LinkedList<Pose> poses) {
        this.lastPoses = poses;
    }
}
