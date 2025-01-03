package bgu.spl.mics.application.objects;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Represents objects detected by the camera at a specific timestamp.
 * Includes the time of detection and a list of detected objects.
 */
public class StampedDetectedObjects {


    private int time;
    private LinkedList<DetectedObject> detectedObjects;

    public StampedDetectedObjects(int time, LinkedList<DetectedObject> detectedObjects) {
        this.time = time;
        this.detectedObjects = detectedObjects;
    }

    public int getTime() {
        return time;
    }

    public int getNumOfDetectedObjects() {
        return detectedObjects.size();
    }

    public LinkedList<DetectedObject> getDetectedObjects() {
        return detectedObjects;
    }

    @Override
    public String toString() {
        return    "\'Detected Objects\':" + "Detection Time: " + time +
                ", detectedObjects = " + detectedObjects;
    }
}
