package bgu.spl.mics.application.objects;

import java.util.LinkedList;

/**
 * Manages the fusion of sensor data for simultaneous localization and mapping (SLAM).
 * Combines data from multiple sensors (e.g., LiDAR, camera) to build and update a global map.
 * Implements the Singleton pattern to ensure a single instance of FusionSlam exists.
 */
public class FusionSlam {
    private final LinkedList<Pose> poseList;
    private final LinkedList<LandMark> landMarkLinkedList;
    private StatisticalFolder statisticalFolder;

    // Singleton instance holder
    private static class FusionSlamHolder {
        private static volatile FusionSlam instance;

    }

    private FusionSlam(StatisticalFolder statisticalFolder) {
        poseList = new LinkedList<>();
        landMarkLinkedList = new LinkedList<>();
        this.statisticalFolder = statisticalFolder;
    }

    public static FusionSlam getInstance(StatisticalFolder statisticalFolder) {
        synchronized (FusionSlamHolder.class) {
            if (FusionSlamHolder.instance == null) {
                FusionSlamHolder.instance = new FusionSlam(statisticalFolder);
            }
            return FusionSlamHolder.instance;
        }
    }

    public void addPose(Pose pose) {
        synchronized (poseList) {
            poseList.add(pose);
        }
    }

    public void addLandMark(LandMark landMark) {
        synchronized (landMarkLinkedList) {
            landMarkLinkedList.add(landMark);
        }
    }
}
