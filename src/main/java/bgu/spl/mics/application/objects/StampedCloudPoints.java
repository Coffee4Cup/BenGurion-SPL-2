package bgu.spl.mics.application.objects;

import java.util.LinkedList;

/**
 * Represents a group of cloud points corresponding to a specific timestamp.
 * Used by the LiDAR system to store and process point cloud data for tracked objects.
 */
public class StampedCloudPoints {
    private int time;
    private String id;
    private Double[][] cloudPoints;
    private volatile LinkedList<CloudPoint> cloudPointsList;

    public StampedCloudPoints(int time, String id, Double[][] cloudPoints) {
        this.time = time;
        this.id = id;
        this.cloudPoints = cloudPoints;
    }

    public int getTime() {
        return time;
    }
    public String getId() {
        return id;
    }
    public synchronized LinkedList<CloudPoint> getCloudPoint(){
        if(cloudPointsList == null){
            cloudPointsList = new LinkedList<>();
            cloudPointsList.add(new CloudPoint(cloudPoints[0][0], cloudPoints[0][1]));
            cloudPointsList.add(new CloudPoint(cloudPoints[1][0], cloudPoints[1][1]));
       /**     for (Double[] point : cloudPoints) {
                cloudPointsList.add(new CloudPoint(point[0], point[1]));
            }*/
        }
        return cloudPointsList;
    }

    public String toString(){
        return "Time: "+time+", ID: "+id+", CloudPoints: "+cloudPointsList;
    }
}
