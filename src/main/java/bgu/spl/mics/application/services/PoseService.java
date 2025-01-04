package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.GPSIMU;
import bgu.spl.mics.application.objects.Pose;
import bgu.spl.mics.application.objects.STATUS;

/**
 * PoseService is responsible for maintaining the robot's current pose (position and orientation)
 * and broadcasting PoseEvents at every tick.
 */
public class PoseService extends MicroService {

    private final GPSIMU gpsimu;
    /**
     * Constructor for PoseService.
     *
     * @param gpsimu The GPSIMU object that provides the robot's pose data.
     */
    public PoseService(GPSIMU gpsimu) {
        super("poseService");
        this.gpsimu = gpsimu;
    }

    /**
     * Initializes the PoseService.
     * Subscribes to TickBroadcast and sends PoseEvents at every tick based on the current pose.
     */
    @Override
    protected void initialize() {
        subscribeBroadcast(TickBroadcast.class, t-> {
            Pose newPose = gpsimu.getPose(t.getTick());
            if(newPose != null){
                sendEvent(new PoseEvent(newPose));
            }
            if(gpsimu.getStatus() != STATUS.UP){
                terminate();
                sendBroadcast(new TerminatedBroadcast(this));
            }
        });
        subscribeBroadcast(CrashedBroadcast.class, c-> {
            terminate();
            sendBroadcast(new TerminatedBroadcast(this));
    //        System.out.println("Terminated PoseService");
        });
        subscribeBroadcast(TerminatedBroadcast.class, t-> {
            if(t.getService() instanceof TimeService){
                terminate();
                sendBroadcast(new TerminatedBroadcast(this));
    //            System.out.println("Terminated PoseService");
            }
        });
        gpsimu.setStatus(STATUS.UP);
        Future<Boolean> start = sendEvent(new InitializedEvent(this));
   //     start.get();
    }
}
