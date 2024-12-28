package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
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
        super("clock");
        tickTime = TickTime;
        duration = Duration;
        currentTick = 1;

    }

    /**
     * Initializes the TimeService.
     * Starts broadcasting TickBroadcast messages and terminates after the specified duration.
     */
    @Override
    protected void initialize() {
        subscribeBroadcast(TerminatedBroadcast.class, t->terminate());
        System.out.println("Starting TimeService");
        while(currentTick < duration) {
            try{
                TimeUnit.SECONDS.sleep(tickTime);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            sendBroadcast(new TickBroadcast(currentTick));
            currentTick+=tickTime;
        }
        sendBroadcast(new TerminatedBroadcast());
    }
}
