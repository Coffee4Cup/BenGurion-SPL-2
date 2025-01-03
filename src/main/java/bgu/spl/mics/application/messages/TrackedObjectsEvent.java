package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.application.objects.CloudPoint;
import bgu.spl.mics.application.objects.TrackedObject;

import java.util.LinkedList;

public class TrackedObjectsEvent implements Event<Boolean> {
    private Future<Boolean> answer;
    private LinkedList<TrackedObject> trackedObjects;

    public TrackedObjectsEvent(LinkedList<TrackedObject> trackedObjects) {
        answer = new Future<>();
        this.trackedObjects = trackedObjects;
    }

    public LinkedList<TrackedObject> getTrackedObjects() {
        return trackedObjects;
    }

    @Override
    public Future<Boolean> getFuture() {
        return answer;
    }

    @Override
    public String toString() {
        return "\'TrackingObjects\': " + trackedObjects ;
    }
}
