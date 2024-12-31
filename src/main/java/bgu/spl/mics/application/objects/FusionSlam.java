package bgu.spl.mics.application.objects;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Manages the fusion of sensor data for simultaneous localization and mapping (SLAM).
 * Combines data from multiple sensors (e.g., LiDAR, camera) to build and update a global map.
 * Implements the Singleton pattern to ensure a single instance of FusionSlam exists.
 */
public class FusionSlam {
    private final HashMap<Integer, Pose> poseList;
    private final HashMap<String, LandMark> landMarkLinkedList;
    private StatisticalFolder statisticalFolder;

    // Singleton instance holder
    private static class FusionSlamHolder {
        private static volatile FusionSlam instance;

    }

    private FusionSlam(StatisticalFolder statisticalFolder) {
        poseList = new HashMap<>();
        landMarkLinkedList = new HashMap<>();
        this.statisticalFolder = statisticalFolder;
    }

    public void addLandMarks(int amount){
        statisticalFolder.addLandMarks(amount);
    }

    public static FusionSlam getInstance(StatisticalFolder statisticalFolder) {
        synchronized (FusionSlamHolder.class) {
            if (FusionSlamHolder.instance == null) {
                FusionSlamHolder.instance = new FusionSlam(statisticalFolder);
            }
            return FusionSlamHolder.instance;
        }
    }

    public void setPose(Pose pose) {
        synchronized (poseList) {
            poseList.put(pose.getTime(), pose);
        }
    }

    public Pose getPose(int tick) {
       synchronized (poseList) {
           if (!poseList.containsKey(tick)) {
               return null;
           }
           return poseList.get(tick);
       }
    }

    public void addLandMark(LandMark landMark) {
        synchronized (landMarkLinkedList) {
            landMarkLinkedList.put(landMark.getId(), landMark);
        }
    }

    public boolean isLandMarkExist(String id){
        synchronized (landMarkLinkedList) {
            return landMarkLinkedList.containsKey(id);
        }
    }

    public void finish(){
        statisticalFolder.setFinalLandMarks(new LinkedList<>(landMarkLinkedList.values()));
    }

    public boolean updateMap(LandMark landMark) {
        synchronized (landMarkLinkedList) {
            if(!landMarkLinkedList.containsKey(landMark.getId())){
                landMarkLinkedList.put(landMark.getId(), landMark);
                System.out.println("fslm added new Landmark: "+landMark);
                return true;
            }
            else{
                landMarkLinkedList.get(landMark.getId()).updateCoordinates(landMark);
                System.out.println("fslm updated old Landmark: "+landMarkLinkedList.get(landMark.getId()));
                return false;
            }
        }
    }

}
