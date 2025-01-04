package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
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
    private int currentTick;
    /**
     * Constructor for LiDarService.
     *
     * @param LiDarWorkerTracker A LiDAR Tracker worker object that this service will use to process data.
     */
    public LiDarService(LiDarWorkerTracker LiDarWorkerTracker) {
        super(LiDarWorkerTracker.getName());
        currentTick = 0;
        this.liDarWorkerTracker = LiDarWorkerTracker;
        liDarWorkerTracker.setService(this);
    }

    /**
     * Initializes the LiDarService.
     * Registers the service to handle DetectObjectsEvents and TickBroadcasts,
     * and sets up the necessary callbacks for processing data.
     */
    @Override
    protected void initialize() {
        subscribeEvent(DetectedObjectEvent.class, e-> {
            //new logic
            LinkedList<TrackedObject> newBatch = liDarWorkerTracker.submitJob(e.getStampedDetectedObjects(), currentTick);
            if(newBatch != null){
                Future<Boolean> f = sendEvent(new TrackedObjectsEvent(newBatch));
            }
            if(liDarWorkerTracker.getStatus() != STATUS.UP){
                terminate();
                if(liDarWorkerTracker.getStatus() == STATUS.ERROR)
                    sendBroadcast(new CrashedBroadcast());
                else
                    sendBroadcast(new TerminatedBroadcast(this));
    //            System.out.println("Terminated " + getName());
            }


   /**         //old logic
            if(e.getStampedDetectedObjects() != null)
                jobList.add(e.getStampedDetectedObjects());
            if(!jobList.isEmpty() && jobList.getFirst().getTime() >= currentTick + liDarWorkerTracker.getFrequency() ){
                StampedDetectedObjects sdo = jobList.removeFirst();
                System.out.println("Lidar working on job "+sdo.getTime()+" at "+currentTick);
                LinkedList<DetectedObject> doList = sdo.getDetectedObjects();
                MessageBusImpl.getInstance().complete(e, true);
                LinkedList<TrackedObject> toList = new LinkedList<>();
                for(DetectedObject d : doList){
                    if(d.id() == "error"){
                        liDarWorkerTracker.error(d.description());

                    }

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
            }*/
        });
        subscribeBroadcast(TickBroadcast.class, t-> {
            currentTick = t.getTick();

        });
        subscribeBroadcast(TerminatedBroadcast.class, t-> {
            if(t.getService() instanceof TimeService){
                liDarWorkerTracker.setStatus(STATUS.DOWN);
                sendBroadcast(new TerminatedBroadcast(this));
                terminate();
       //         System.out.println("Terminated "+getName());
            }
        });

        subscribeBroadcast(CrashedBroadcast.class, t->{
            liDarWorkerTracker.setStatus(STATUS.DOWN);
            liDarWorkerTracker.updateLastFrames();
            sendBroadcast(new TerminatedBroadcast(this));
            terminate();
      //      System.out.println("Terminated "+getName());
        });
        liDarWorkerTracker.setStatus(STATUS.UP);
        Future<Boolean> start = sendEvent(new InitializedEvent(this));
     //   start.get();
    }
}
