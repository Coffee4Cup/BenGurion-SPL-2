package bgu.spl.mics.application.objects;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Represents the robot's GPS and IMU system.
 * Provides information about the robot's position and movement.
 */
public class GPSIMU {
    private int currentTick;
    private STATUS status;
    private HashMap<Integer, Pose> poseMap;

    public GPSIMU(LinkedList<Pose> poseList) {
        poseMap = new HashMap<>();
        if(!poseList.isEmpty()) {
            for (Pose pose : poseList) {
                this.poseMap.put(pose.getTime(), pose);
            }
        }
        currentTick = 0;
        status = STATUS.UP;
    }
    public String toString(){
        return "["+poseMap.toString()+"]";
    }

    public Pose getPose(int tick){
        return this.poseMap.get(tick);
    }

}
