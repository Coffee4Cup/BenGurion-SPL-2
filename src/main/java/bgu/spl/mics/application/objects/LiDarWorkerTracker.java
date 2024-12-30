package bgu.spl.mics.application.objects;

import java.util.LinkedList;

/**
 * LiDarWorkerTracker is responsible for managing a LiDAR worker.
 * It processes DetectObjectsEvents and generates TrackedObjectsEvents by using data from the LiDarDataBase.
 * Each worker tracks objects and sends observations to the FusionSlam service.
 */
public class LiDarWorkerTracker {
    private int id;
    private int frequency;
    private STATUS status;
    private String lidars_data_path;
    private LinkedList<TrackedObject> lastTrackedObjects;

    public LiDarWorkerTracker(int id, int frequency, STATUS status, String lidars_data_path) {
        this.id = id;
        this.frequency = frequency;
        this.status = status;
        this.lastTrackedObjects = new LinkedList<>();
        this.lidars_data_path = lidars_data_path;
    }

    public int getId() {
        return id;
    }

    public int getFrequency() {
        return frequency;
    }

    public String toString(){
        return "ID: "+id+", frequency: "+frequency+", status: "+status + " dataPath: "+lidars_data_path +" LastTrackedObjects: "+lastTrackedObjects;
    }
/**
    public CloudPoint getObjectLocation(String id){
        LiDarDataBase.getInstance(lidars_data_path);
        return "fff";

    }*/

    public String getPath(){
        return lidars_data_path;
    }


}
