package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.DetectedObjectEvent;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TrackedObjectsEvent;
import bgu.spl.mics.application.objects.*;

import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

/**
 * LiDarService is responsible for processing data from the LiDAR sensor and
 * sending TrackedObjectsEvents to the FusionSLAM service.
 * 
 * This service interacts with the LiDarWorkerTracker object to retrieve and process
 * cloud point data and updates the system's StatisticalFolder upon sending its
 * observations.
 */
public class LiDarService extends MicroService {
    private final LiDarWorkerTracker liDarWorkerTracker;
    private final LinkedList<StampedDetectedObjects> jobList;
    private final LinkedList<Future<Boolean>> detectionHistory;
    private int currentTick;
    /**
     * Constructor for LiDarService.
     *
     * @param LiDarWorkerTracker A LiDAR Tracker worker object that this service will use to process data.
     */
    public LiDarService(LiDarWorkerTracker LiDarWorkerTracker) {
        super("LidarService");
        currentTick = 0;
        this.liDarWorkerTracker = LiDarWorkerTracker;
        jobList = new LinkedList<>();
        detectionHistory = new LinkedList<>();
    }

    /**
     * Initializes the LiDarService.
     * Registers the service to handle DetectObjectsEvents and TickBroadcasts,
     * and sets up the necessary callbacks for processing data.
     */
    @Override
    protected void initialize() {
        subscribeEvent(DetectedObjectEvent.class, e-> {
            if(e.getStampedDetectedObjects() != null)
                jobList.add(e.getStampedDetectedObjects());
            if(!jobList.isEmpty() && jobList.getFirst().getTime() >= currentTick + liDarWorkerTracker.getFrequency() ){
                StampedDetectedObjects sdo = jobList.removeFirst();
                System.out.println("Lidar working on job "+sdo.getTime()+" at "+currentTick);
                LinkedList<DetectedObject> doList = sdo.getDetectedObjects();
                MessageBusImpl.getInstance().complete(e, true);
                LinkedList<TrackedObject> toList = new LinkedList<>();
                for(DetectedObject d : doList){
                    StampedCloudPoints scp = LiDarDataBase.getInstance(liDarWorkerTracker.getPath()).getStampedCloudPoints(sdo.getTime()+d.id());
                    if (scp != null) {
                        TrackedObject to = new TrackedObject(d.id(), sdo.getTime(), d.description(), scp.getCloudPoint());
                        System.out.println("found scp: " + scp);
                        toList.add(to);
                    }
                }
                Future<Boolean> f = sendEvent(new TrackedObjectsEvent(toList));
                liDarWorkerTracker.addTrackedObjects(toList.size());
                detectionHistory.add(f);
            }
        });
        subscribeBroadcast(TickBroadcast.class, t-> {
            currentTick = t.getTick();

        });
        subscribeBroadcast(TerminatedBroadcast.class, t-> {
            terminate();
        });
    }
}
