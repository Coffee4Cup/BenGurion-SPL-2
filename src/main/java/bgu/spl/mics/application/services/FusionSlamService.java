package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.PoseEvent;
import bgu.spl.mics.application.objects.*;

import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TrackedObjectsEvent;

import java.util.LinkedList;

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
        super("fusionSlamService");
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
            Pose currentPose = fusion.getCurrentPose();
            for(TrackedObject to : e.getTrackedObjects()){
                LinkedList<CloudPoint> localCloudPoints = to.getCoordinates();
                //Calculating landmarks in global coordinates
                float yawRad = currentPose.getYaw() * (float) Math.PI / 180;
                float cosRad = (float) Math.cos(yawRad);
                float sinRad = (float) Math.sin(yawRad);
                CloudPoint local1 = localCloudPoints.get(0);
                CloudPoint local2 = localCloudPoints.get(1);
                CloudPoint global1 = new CloudPoint(
                        currentPose.getX() + local1.x() * cosRad - local1.y() * sinRad,
                        currentPose.getY() + local1.x() * sinRad + local1.y() * cosRad);
                CloudPoint global2 = new CloudPoint(
                        currentPose.getX() + local2.x() * cosRad - local2.y() * sinRad,
                        currentPose.getY() + local2.x() * sinRad + local2.y() * cosRad);
                //fusion.updateMap returns true if new landmark, else false
                //if previously detected, updates coordinates to averages
                if(fusion.updateMap(new LandMark(to.getId(), to.getDescription(), global1, global2)))
                    statisticalFolder.addLandMarks(1);
            }
        });

        subscribeEvent(PoseEvent.class, e-> {
            fusion.setPose(e.getFuture().get());
        });
        subscribeBroadcast(TerminatedBroadcast.class, t-> {
            terminate();
            System.out.println("Terminated FusionSlamService");
        });
    }
}
