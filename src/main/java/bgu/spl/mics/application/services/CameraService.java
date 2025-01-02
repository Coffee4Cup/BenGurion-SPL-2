package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.DetectedObjectEvent;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.objects.DetectedObject;
import bgu.spl.mics.application.objects.StampedDetectedObjects;

import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

/**
 * CameraService is responsible for processing data from the camera and
 * sending DetectObjectsEvents to LiDAR workers.
 * 
 * This service interacts with the Camera object to detect objects and updates
 * the system's StatisticalFolder upon sending its observations.
 */
public class CameraService extends MicroService {
    private Camera camera;
    private LinkedList<Future<Boolean>> detectionHistory;
    /**
     * Constructor for CameraService.
     *
     * @param camera The Camera object that this service will use to detect objects.
     */
    public CameraService(Camera camera) {
        super("camera");
        this.camera = camera;
        detectionHistory = new LinkedList<>();
    }

    /**
     * Initializes the CameraService.
     * Registers the service to handle TickBroadcasts and sets up callbacks for sending
     * DetectObjectsEvents.
     */
    @Override
    protected void initialize() {
        System.out.println("Initializing CameraService");
        subscribeBroadcast(TickBroadcast.class, t -> {
            StampedDetectedObjects sdo = camera.getStampedDetectedObjects(t.getTick());
            for(DetectedObject d  : sdo.getDetectedObjects()) {
                detectionHistory.add(sendEvent(new DetectedObjectEvent( d )));
            }
        });
        subscribeBroadcast(TerminatedBroadcast.class, t -> {
            for(Future<Boolean> f : detectionHistory) {
                if(f.get(10, TimeUnit.MILLISECONDS) != null){
                    if(f.get())
                        camera.objectDetected();
                }
            }
            terminate();
            System.out.println("Terminated CameraService, score is "+ camera.getNumOfDetectedObjects());
        });
    }

}
