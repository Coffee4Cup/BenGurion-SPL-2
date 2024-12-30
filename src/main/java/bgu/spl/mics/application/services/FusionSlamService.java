package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.PoseEvent;
import bgu.spl.mics.application.objects.FusionSlam;
import bgu.spl.mics.application.objects.StatisticalFolder;

import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TrackedObjectsEvent;

/**
 * FusionSlamService integrates data from multiple sensors to build and update
 * the robot's global map.
 * 
 * This service receives TrackedObjectsEvents from LiDAR workers and PoseEvents from the PoseService,
 * transforming and updating the map with new landmarks.
 */
public class FusionSlamService extends MicroService {

    private final StatisticalFolder statisticalFolder;
    private final FusionSlam fusion;
    /**
     * Constructor for FusionSlamService.
     *
     * @param fusionSlam The FusionSLAM object responsible for managing the global map.
     */
    public FusionSlamService(FusionSlam fusionSlam, StatisticalFolder statisticalFolder) {
        super("Change_This_Name");
        this.statisticalFolder = statisticalFolder;
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
            statisticalFolder.addTrackedObjects(1);
        });

        subscribeEvent(PoseEvent.class, e-> {
            fusion.addPose(e.getFuture().get());
        });
     /**   subscribeBroadcast(TickBroadcast.class, t-> {
            fusion.updateMap();
        });*/
        subscribeBroadcast(TerminatedBroadcast.class, t-> {
            terminate();
            System.out.println("Terminated FusionSlamService");
        });
    }
}
