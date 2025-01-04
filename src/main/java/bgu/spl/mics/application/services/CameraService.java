package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.*;

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
        super(camera.getName());
        this.camera = camera;
        camera.setService(this);
        detectionHistory = new LinkedList<>();
    }

    /**
     * Initializes the CameraService.
     * Registers the service to handle TickBroadcasts and sets up callbacks for sending
     * DetectObjectsEvents.
     */
    @Override
    protected void initialize() {
    //    System.out.println("Initializing CameraService");
        subscribeBroadcast(TickBroadcast.class, t -> {
        //    System.out.println(getName() + " is processing tick "+t.getTick());
            StampedDetectedObjects sdo = camera.getDetectedObjectList(t.getTick());
            if(sdo != null){
                Future<Boolean> f = sendEvent(new DetectedObjectEvent(sdo));
                detectionHistory.add(f);
            }

            if(camera.getStatus() != STATUS.UP){
                terminate();
                if(camera.getStatus() == STATUS.ERROR)
                    sendBroadcast(new CrashedBroadcast());
                else
                    sendBroadcast(new TerminatedBroadcast(this));
            }

        });
        subscribeBroadcast(TerminatedBroadcast.class, t -> {
            if(t.getService() instanceof TimeService){
                camera.setStatus(STATUS.DOWN);
                terminate();
                sendBroadcast(new TerminatedBroadcast(this));
            }
        //    System.out.println("Terminated CameraService, score is ");
        });
        subscribeBroadcast(CrashedBroadcast.class, t->{
            camera.updateLastFrames();
            camera.setStatus(STATUS.DOWN);
            sendBroadcast(new TerminatedBroadcast(this));
            terminate();
        });
        camera.setStatus(STATUS.UP);
        Future<Boolean> start = sendEvent(new InitializedEvent(this));
    //    start.get();

    }

}
