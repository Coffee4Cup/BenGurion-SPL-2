package bgu.spl.mics.application.objects;

import java.util.LinkedList;

/**
 * Represents a landmark in the environment map.
 * Landmarks are identified and updated by the FusionSlam service.
 */
public class LandMark {
    private String id;
    private String description;
    private LinkedList<CloudPoint> coordinates;

    public LandMark(String id, String description, LinkedList<CloudPoint> coordinates) {
        this.id = id;
        this.description = description;
        this.coordinates = coordinates;
    }

    public LandMark(String id, String description, CloudPoint global1, CloudPoint global2) {
        this.id = id;
        this.description = description;
        this.coordinates = new LinkedList<>();
        this.coordinates.add(global1);
        this.coordinates.add(global2);
    }

    public String getId() {
        return id;
    }

    public void updateCoordinates(LandMark newDetection) {
        this.coordinates.get(0).update(newDetection.coordinates.get(0));
        this.coordinates.get(1).update(newDetection.coordinates.get(1));
    }

    public String toString(){
        return "ID: "+id+ ", Coordinates: "+coordinates;
    }
}
