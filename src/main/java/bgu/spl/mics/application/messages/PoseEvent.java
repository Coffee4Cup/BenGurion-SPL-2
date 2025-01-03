package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.application.objects.Pose;

public class PoseEvent implements Event<Pose> {

    private Future<Pose> future;
    private Pose newPose;

    public PoseEvent(Pose pose) {
        newPose = pose;
        this.future = new Future<>();
        future.resolve(pose);
    }

    public Pose getPose(){
        return newPose;
    }

    @Override
    public Future<Pose> getFuture() {
        return future;
    }

    @Override
    public String toString() {
        return "PoseEvent, new Pose: " + newPose;

    }
}
