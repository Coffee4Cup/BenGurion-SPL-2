import bgu.spl.mics.*;

import java.util.ArrayList;

/**
 * Dummy implementation of {@link MessageBus} for testing purposes.
 */

public class MessageBusTestImpl implements MessageBus {
    ArrayList<ArrayList<MicroService>> eventSubscriptions;
    ArrayList<ArrayList<MicroService>> broadcastSubscriptions;
    ArrayList<ArrayList<Event>> eventSubs;

    private static class MessageBusHolder{
        private static final MessageBusTestImpl instance = new MessageBusTestImpl();
    }

    private MessageBusTestImpl() {
        this.eventSubscriptions = new ArrayList<>();
        this.broadcastSubscriptions = new ArrayList<>();
        this.eventSubs = new ArrayList<>();
    }

    public static MessageBus getInstance() {
        return MessageBusHolder.instance;
    }

    @Override
    public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {

    }

    @Override
    public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {

    }

    @Override
    public <T> void complete(Event<T> e, T result) {

    }

    @Override
    public void sendBroadcast(Broadcast b) {

    }

    @Override
    public <T> Future<T> sendEvent(Event<T> e) {
        return null;
    }

    @Override
    public void register(MicroService m) {

    }

    @Override
    public void unregister(MicroService m) {

    }

    @Override
    public Message awaitMessage(MicroService m) throws InterruptedException {
        return null;
    }
}
