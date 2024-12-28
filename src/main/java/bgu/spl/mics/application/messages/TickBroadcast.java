package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;
import java.util.concurrent.TimeUnit;

public class TickBroadcast implements Broadcast {
    int tick;
    public TickBroadcast(int unit) {
        this.tick = unit;
    }

    public int getTick() {
        return tick;
    }
}
