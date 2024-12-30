package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.application.objects.CloudPoint;
import bgu.spl.mics.application.objects.TrackedObject;

public class TrackedObjectsEvent implements Event<Boolean> {
    private Future<Boolean> answer;
    private TrackedObject trackedObject;

    public TrackedObjectsEvent(TrackedObject trackedObject) {
        answer = new Future<>();
        this.trackedObject = trackedObject;
    }

    public TrackedObject getTrackedObject() {
        return trackedObject;
    }

    @Override
    public Future<Boolean> getFuture() {
        return null;
    }
}
