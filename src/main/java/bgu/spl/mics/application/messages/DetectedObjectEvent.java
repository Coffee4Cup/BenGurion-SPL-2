package bgu.spl.mics.application.messages;
import bgu.spl.mics.Event;
import bgu.spl.mics.Future;

public class DetectedObjectEvent implements Event<Integer> {

    private Future<Integer> timeDetected;

    @Override
    public Future<Integer> getFuture() {
        return timeDetected;
    }
}
