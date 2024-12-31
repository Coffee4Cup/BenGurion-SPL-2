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
import bgu.spl.mics.application.objects.StatisticalFolder;

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
    private final Camera camera;
    private LinkedList<Future<Boolean>> detectionHistory;
    /**
     * Constructor for CameraService.
     *
     * @param camera The Camera object that this service will use to detect objects.
     */
    public CameraService(Camera camera) {
        super("cameraService");
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
            StampedDetectedObjects sdo = camera.getDetectedObjectList(t.getTick() - camera.getFrequency());
            if(sdo != null){
                camera.objecstDetected(sdo.getNumOfDetectedObjects());
                Future<Boolean> f = sendEvent(new DetectedObjectEvent(sdo));
                detectionHistory.add(f);
            }
        });
        subscribeBroadcast(TerminatedBroadcast.class, t -> {

            terminate();
            System.out.println("Terminated CameraService, score is ");
        });
    }

}
