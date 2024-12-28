package bgu.spl.mics.application.objects;

import java.util.LinkedList;

/**
 * Represents a group of cloud points corresponding to a specific timestamp.
 * Used by the LiDAR system to store and process point cloud data for tracked objects.
 */
public class StampedCloudPoints {
    private int time;
    private String id;
    private LinkedList<CloudPoint> cloudPoints;

    public StampedCloudPoints(int time, String id, LinkedList<CloudPoint> cloudPoints) {
        this.time = time;
        this.id = id;
        this.cloudPoints = cloudPoints;
    }
}
