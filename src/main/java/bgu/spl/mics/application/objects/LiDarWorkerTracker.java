package bgu.spl.mics.application.objects;

import bgu.spl.mics.Future;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.application.messages.TrackedObjectsEvent;
import bgu.spl.mics.application.services.LiDarService;

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
    private final LinkedList<StampedDetectedObjects> jobList;
    private LinkedList<TrackedObject> lastTrackedObjects;
    private final StatisticalFolder statisticalFolder;
    private LiDarService service;

    public LiDarWorkerTracker(int id, int frequency, String lidars_data_path, StatisticalFolder statisticalFolder) {
        this.id = id;
        this.frequency = frequency;
        this.status = STATUS.DOWN;
        this.lastTrackedObjects = new LinkedList<>();
        this.lidars_data_path = lidars_data_path;
        this.statisticalFolder = statisticalFolder;
        jobList = new LinkedList<>();
    }

    public void setService(LiDarService service){
        this.service = service;
    }

    public LinkedList<TrackedObject> submitJob(StampedDetectedObjects stampedDetectedObjects, int currentTick) {
        if (stampedDetectedObjects != null)
            jobList.add(stampedDetectedObjects);
        if (!jobList.isEmpty() && jobList.getFirst().getTime() >= currentTick + frequency) {
            StampedDetectedObjects sdo = jobList.removeFirst();
            System.out.println("Lidar working on job " + sdo.getTime() + " at " + currentTick);
            LinkedList<DetectedObject> doList = sdo.getDetectedObjects();
            LinkedList<TrackedObject> toList = new LinkedList<>();
            for (DetectedObject d : doList) {
                if (d.id() == "ERROR") {
                    error(d.description());
                    return null;
                }
                StampedCloudPoints scp = LiDarDataBase.getInstance(lidars_data_path).getStampedCloudPoints(sdo.getTime() + d.id());
                if (LiDarDataBase.getInstance(lidars_data_path).isDone()) {
                    status = STATUS.DOWN;
                }
                if (scp != null) {
                    TrackedObject to = new TrackedObject(d.id(), sdo.getTime(), d.description(), scp.getCloudPoint());
                    System.out.println("found scp: " + scp);
                    toList.add(to);
                }
            }
            statisticalFolder.addTrackedObjects(toList.size());
            lastTrackedObjects = toList;
            return toList;
        }
        return null;
    }

    public STATUS getStatus() {
        return status;
    }

    public void error(String description){
        setStatus(STATUS.ERROR);
        statisticalFolder.setErrorDescription(description);
        updateLastFrames();
    }

    public void updateLastFrames(){
        statisticalFolder.setTrackedObjects(lastTrackedObjects);
    }


    public int getId() {
        return id;
    }

    public void addTrackedObjects(int amount){
        statisticalFolder.addTrackedObjects(amount);
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


    public void setStatus(STATUS status) {
        this.status = status;
    }
}
