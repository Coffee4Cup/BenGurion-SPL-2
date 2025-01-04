package bgu.spl.mics.application.services;

import bgu.spl.mics.Event;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.*;

import java.util.LinkedList;

/**
 * FusionSlamService integrates data from multiple sensors to build and update
 * the robot's global map.
 * 
 * This service receives TrackedObjectsEvents from LiDAR workers and PoseEvents from the PoseService,
 * transforming and updating the map with new landmarks.
 */
public class FusionSlamService extends MicroService {

    private final FusionSlam fusion;

    /**
     * Constructor for FusionSlamService.
     *
     * @param fusionSlam The FusionSLAM object responsible for managing the global map.
     */
    public FusionSlamService(FusionSlam fusionSlam) {
        super("fusionSlamService");
        this.fusion = fusionSlam;
    }

    /**
     * Initializes the FusionSlamService.
     * Registers the service to handle TrackedObjectsEvents, PoseEvents, and TickBroadcasts,
     * and sets up callbacks for updating the global map.
     */
    @Override
    protected void initialize() {
        subscribeEvent(TrackedObjectsEvent.class, e-> {
            fusion.calculate(e.getTrackedObjects());
        });

        subscribeEvent(InitializedEvent.class, e-> {
            InitializedEvent initialization = fusion.startProcessUsingIndicators(e);
            if(initialization != null){
                complete(initialization, true);
            }
        });

        subscribeEvent(PoseEvent.class, e-> {
            fusion.setPose(e.getPose());
        });
        subscribeBroadcast(CrashedBroadcast.class, c-> {
            fusion.crash();
            terminate();
        //    System.out.println("Terminated FusionSlamService");
        });
        subscribeBroadcast(TerminatedBroadcast.class, t-> {
            if(t.getService() instanceof CameraService){
                fusion.cameraTerminated();
            }
            else if(t.getService() instanceof LiDarService){
                fusion.lidarTerminated();
            }
            else if(t.getService() instanceof PoseService){
                fusion.GPSTerminated();
            }
            if(t.getService() instanceof TimeService || fusion.checkAndTerminate()) {
                fusion.finish();
                terminate();
                sendBroadcast(new TerminatedBroadcast(this));
            }
        //    System.out.println("Terminated FusionSlamService");
        });
    }
}
