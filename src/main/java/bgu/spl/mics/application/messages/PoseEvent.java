package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.application.objects.Pose;

public class PoseEvent implements Event<Pose> {

    private Future<Pose> future;

    public PoseEvent(Pose pose) {
        this.future = new Future<>();
        future.resolve(pose);
    }

    @Override
    public Future<Pose> getFuture() {
        return future;
    }

}
