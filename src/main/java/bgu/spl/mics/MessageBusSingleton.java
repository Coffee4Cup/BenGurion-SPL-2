package bgu.spl.mics;

public class MessageBusSingleton {
    private static MessageBus instance;

    private MessageBusSingleton() {}

    public static MessageBus getInstance() {
        if (instance == null) {
            throw new IllegalStateException("MessageBus implementation not set");
        }
        return instance;
    }

    public static void setInstance(MessageBus messageBus) {
        if (instance != null) {
            throw new IllegalStateException("MessageBus implementation already set");
        }
        instance = messageBus;
    }
}
