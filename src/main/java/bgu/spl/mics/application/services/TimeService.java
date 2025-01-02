package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
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
        System.out.println("Starting TimeService");
        Future<Boolean> start = sendEvent(new InitializedEvent(this));
        start.get();
        try{

            while(currentTick < duration) {
                TimeUnit.SECONDS.sleep(tickTime);
                sendBroadcast(new TickBroadcast(currentTick));
                currentTick+=tickTime;
            }

        }catch (InterruptedException e) {
            System.out.println("Clock stopped");
        }
        sendBroadcast(new TerminatedBroadcast());
    }
}
