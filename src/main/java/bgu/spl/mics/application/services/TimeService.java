package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;

import static java.lang.Thread.sleep;

/**
 * TimeService acts as the global timer for the system, broadcasting TickBroadcast messages
 * at regular intervals and controlling the simulation's duration.
 */
public class TimeService extends MicroService {

    private int tickTime;
    private int duration;
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
    }

    /**
     * Initializes the TimeService.
     * Starts broadcasting TickBroadcast messages and terminates after the specified duration.
     */
    @Override
    protected void initialize() {
        subscribeBroadcast(TerminatedBroadcast.class, t->terminate());
        System.out.println("Starting TimeService");
        while(duration > 0){
            try{
                sleep(tickTime);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            sendBroadcast(new TickBroadcast(tickTime));
            duration--;
        }
        sendBroadcast(new TerminatedBroadcast());
    }
}
