package bgu.spl.mics.application.messages;
import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.application.objects.DetectedObject;

public class DetectedObjectEvent implements Event<Boolean> {

    private Future<Boolean> answer;
    private DetectedObject detectedObject;

    public DetectedObjectEvent(DetectedObject detectedObject) {
        this.detectedObject = detectedObject;
    }
    @Override
    public Future<Boolean> getFuture() {

        return answer;
    }

    public DetectedObject getDetectedObject() {
        return detectedObject;
    }
}
