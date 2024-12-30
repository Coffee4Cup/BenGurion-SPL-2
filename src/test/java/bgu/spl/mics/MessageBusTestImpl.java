package bgu.spl.mics;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Dummy implementation of {@link MessageBus} for testing purposes.
 */

public class MessageBusTestImpl implements MessageBus {


    private ArrayList<MicroService> ArrayDummyBroadcast;

    private static class MessageBusHolder{
        private static final MessageBusTestImpl instance = new MessageBusTestImpl();
    }

    public MessageBusTestImpl() {
        ArrayDummyBroadcast = new ArrayList<>();
    }

    public static MessageBus getInstance() {
        return MessageBusHolder.instance;
    }

    @Override
    public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {


    }

    @Override
    public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
        ArrayDummyBroadcast.add(m);
    }

    @Override
    public <T> void complete(Event<T> e, T result) {

    }

    @Override
    public void sendBroadcast(Broadcast b) {
        for (MicroService m : ArrayDummyBroadcast) {
            synchronized (m) {
                m.notifyAll();
            }
        }
    }

    @Override
    public <T> Future<T> sendEvent(Event<T> e) {
        return null;
    }

    @Override
    public void register(MicroService m) {
        System.out.println("registering " + m.getName());

    }

    @Override
    public void unregister(MicroService m) {

    }

    @Override
    public Message awaitMessage(MicroService m) throws InterruptedException {
        synchronized (m) {
            m.wait();
        }
        return null;
    }
}
