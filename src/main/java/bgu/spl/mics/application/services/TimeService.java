package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.InitializedEvent;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;

import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;

/**
 * TimeService acts as the global timer for the system, broadcasting TickBroadcast messages
 * at regular intervals and controlling the simulation's duration.
 */
public class TimeService extends MicroService {

    private int tickTime;
    private int duration;
    private int currentTick;
    /**
     * Constructor for TimeService.
     *
     * @param TickTime  The duration of each tick in milliseconds.
     * @param Duration  The total number of ticks before the service terminates.
     */
    public TimeService(int TickTime, int Duration) {
        super("timeService");
        tickTime = TickTime;
        duration = Duration;
        currentTick = 1;

    }

    public int getCurrentTick(){
        return currentTick;
    }

    /**
     * Initializes the TimeService.
     * Starts broadcasting TickBroadcast messages and terminates after the specified duration.
     */
    @Override
    protected void initialize() {
        subscribeBroadcast(TerminatedBroadcast.class, t->terminate());
    //    System.out.println("Starting TimeService");
        subscribeBroadcast(TickBroadcast.class, t->{
            try{
                TimeUnit.SECONDS.sleep(tickTime);
            } catch (InterruptedException e) {
                terminate();
                sendBroadcast(new TerminatedBroadcast(this));
            }
            currentTick = t.getTick()+1;
            sendBroadcast(new TickBroadcast(currentTick));
            if(currentTick >= duration){
                terminate();
                sendBroadcast(new TerminatedBroadcast(this));
            }
        });
        subscribeBroadcast(TerminatedBroadcast.class, t->{
            if(t.getService() instanceof FusionSlamService){
                terminate();
                sendBroadcast(new TerminatedBroadcast(this));
            }
        });
        subscribeBroadcast(CrashedBroadcast.class, c->{
            terminate();
            sendBroadcast(new TerminatedBroadcast(this));
        });
        Future<Boolean> start = sendEvent(new InitializedEvent(this));
        start.get();
        sendBroadcast(new TickBroadcast(currentTick));
   /**     try{

            while(currentTick < duration) {
                TimeUnit.SECONDS.sleep(tickTime);
                sendBroadcast(new TickBroadcast(currentTick));
                currentTick++;
            }

        }catch (InterruptedException e) {
    //        System.out.println("Clock stopped");
        }
    terminate();
    sendBroadcast(new TerminatedBroadcast(this));*/
    }
}
