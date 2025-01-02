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
    private int finalTick;
    private boolean isDone;

    public GPSIMU(LinkedList<Pose> poseList) {
        poseMap = new HashMap<>();
        if(!poseList.isEmpty()) {
            for (Pose pose : poseList) {
                if(pose.getTime() > finalTick){
                    finalTick = pose.getTime();
                }
                this.poseMap.put(pose.getTime(), pose);
            }
        }
        currentTick = 0;
        status = STATUS.DOWN;
    }
    public STATUS getStatus(){
        return status;
    }
    public void setStatus(STATUS status){
        this.status = status;
    }
    public boolean isDone(){
        return isDone;
    }
    public String toString(){
        return "["+poseMap.toString()+"]";
    }

    public Pose getPose(int tick){
        if(tick > finalTick){
            isDone = true;
            status = STATUS.DOWN;
            return null;
        }
        return this.poseMap.get(tick);
    }

}
