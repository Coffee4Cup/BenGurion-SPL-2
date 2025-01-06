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
        //new logic for more than 2 points
   //     System.out.println("Current calculation:"+coordinates.size());
   //     System.out.println("new calculation:"+newDetection.coordinates.size());
        for(int i=0; i<coordinates.size() && i<newDetection.coordinates.size(); i++){
            coordinates.get(i).update(newDetection.coordinates.get(i));
        }
        for(int i=coordinates.size(); i<newDetection.coordinates.size(); i++){
            coordinates.add(newDetection.coordinates.get(i));
        }
/**     old logic for 2 points
        this.coordinates.get(0).update(newDetection.coordinates.get(0));
        this.coordinates.get(1).update(newDetection.coordinates.get(1)); */
    }

    //Used only for TESTS

    public LinkedList<CloudPoint> getCoordinates(){
        return coordinates;
    }

    public String toString(){
        return "ID: "+id+ ", Coordinates: "+coordinates;
    }
}
