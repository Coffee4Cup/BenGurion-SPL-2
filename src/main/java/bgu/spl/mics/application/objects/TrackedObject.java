package bgu.spl.mics.application.objects;

import java.util.LinkedList;

/**
 * Represents an object tracked by the LiDAR.
 * This object includes information about the tracked object's ID, description, 
 * time of tracking, and coordinates in the environment.
 */
public class TrackedObject {
    private String id;
    private int time;
    private String description;
    private LinkedList<CloudPoint> coordinates;

    public TrackedObject(String id, int time, String description, LinkedList<CloudPoint> coordinates) {
        this.id = id;
        this.time = time;
        this.description = description;
        this.coordinates = coordinates;
    }
    public String getId() {
        return id;
    }
    public int getTime() {
        return time;
    }
    public String getDescription() {
        return description;
    }
    public LinkedList<CloudPoint> getCoordinates() {
        return coordinates;
    }

    @Override
    public String toString() {
        return  "id='" + id + '\'' +
                ", time=" + time +
                ", description='" + description + '\'' +
                ", coordinates=" + coordinates;
    }
}
