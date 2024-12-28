package bgu.spl.mics.application.objects;

import java.util.LinkedList;

/**
 * Represents the robot's GPS and IMU system.
 * Provides information about the robot's position and movement.
 */
public class GPSIMU {
    private int currentTick;
    private STATUS status;
    private LinkedList<Pose> poseList;

    public GPSIMU(LinkedList<Pose> poseList) {
        this.poseList = poseList;
        currentTick = 0;
        status = STATUS.UP;
    }
    public String toString(){
        return "["+poseList.toString()+"]";
    }
}
