package bgu.spl.mics.application.objects;

import com.google.gson.Gson;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
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
    private HashMap<String, StampedDetectedObjects> cameraLastFrames;
    private HashMap<String, LinkedList<TrackedObject>> lidarLastFrames;
    private String errorDescription;
    private String faultySensor;
    private LinkedList<Pose> lastPoses;
    private boolean error;

    private LinkedList<LandMark> landmarks;

    public StatisticalFolder(){
        systemRunTime = new AtomicInteger(0);
        numDetectedObjects = new AtomicInteger(0);
        numTrackedObjects = new AtomicInteger(0);
        numLandmarks = new AtomicInteger(0);
        cameraLastFrames = new HashMap<>();
        lidarLastFrames = new HashMap<>();
        error = false;
    }

    public void setErrorDescription(String errorDescription, String faultySensor){
        error = true;
        this.faultySensor = faultySensor;
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

    public void setCameraLastFrames(StampedDetectedObjects lastFrames, String camera) {
        cameraLastFrames.put(camera, lastFrames);
    }

    public void setTrackedObjects(LinkedList<TrackedObject> lastTrackedObjects, String lidar) {
        lidarLastFrames.put(lidar, lastTrackedObjects);
    }

    public void setLastPoses(LinkedList<Pose> poses) {
        this.lastPoses = poses;
    }


    private class ErrorOutput{
        private String error;
        private String faultySensor;
        private HashMap<String,StampedDetectedObjects> lastCamerasFrame;
        private HashMap<String, LinkedList<TrackedObject>> lastLiDarWorkerTrackersFrame;
        private LinkedList<Pose> poses;
        private Output statistics;
        private ErrorOutput(StatisticalFolder statisticalFolder){
            this.error = statisticalFolder.errorDescription;
            this.faultySensor = statisticalFolder.faultySensor;
            this.lastCamerasFrame = statisticalFolder.cameraLastFrames;
            this.lastLiDarWorkerTrackersFrame = statisticalFolder.lidarLastFrames;
            this.poses = statisticalFolder.lastPoses;
            this.statistics = new Output(statisticalFolder);
        }
    }
    private class Output{
        private int systemRunTime;
        private int numDetectedObjects;
        private int numTrackedObjects;
        private int numLandmarks;
        private LinkedList<LandMark> landMarks;
        private Output(StatisticalFolder statisticalFolder){
            this.systemRunTime = statisticalFolder.systemRunTime.get();
            this.numDetectedObjects = statisticalFolder.numDetectedObjects.get();
            this.numTrackedObjects = statisticalFolder.numTrackedObjects.get();
            this.numLandmarks = statisticalFolder.numLandmarks.get();
            this.landMarks = statisticalFolder.landmarks;
        }
    }
    public void output(Gson gson, String directory) {
        if (error) {
            try (FileWriter writer = new FileWriter(directory + "/OutputERROR.json")) {
                ErrorOutput errorOutput = new ErrorOutput(this);
                gson.toJson(errorOutput, writer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try (FileWriter writer = new FileWriter(directory + "/output_file.json")) {
                Output output = new Output(this);
                gson.toJson(output, writer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
