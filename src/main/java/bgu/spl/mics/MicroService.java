package bgu.spl.mics;

import java.util.HashMap;

/**
 * The MicroService is an abstract class that any micro-service in the system
 * must extend. The abstract MicroService class is responsible to get and
 * manipulate the singleton {@link MessageBus} instance.
 * <p>
 * Derived classes of MicroService should never directly touch the message-bus.
 * Instead, they have a set of internal protected wrapping methods (e.g.,
 * {@link #sendBroadcast(bgu.spl.mics.Broadcast)}, {@link #sendBroadcast(bgu.spl.mics.Broadcast)},
 * etc.) they can use. When subscribing to message-types,
 * the derived class also supplies a {@link Callback} that should be called when
 * a message of the subscribed type was taken from the micro-service
 * message-queue (see {@link MessageBus#register(bgu.spl.mics.MicroService)}
 * method). The abstract MicroService stores this callback together with the
 * type of the message is related to.
 * 
 * Only private fields and methods may be added to this class.
 * <p>
 */
/**
 * Class Invariants:
 *
 * 1. Field: `terminated`
 *    - The `terminated` flag must always start as `false` upon initialization of the `MicroService`.
 *    - It remains `false` until explicitly set to `true` by a call to the `terminate()` method, which signals the event loop to stop execution.
 *    - Once `true`, no further messages are processed by the `run()` method.
 *
 * 2. Field: `eventCallbacks`
 *    - The `eventCallbacks` map **always contains valid mappings** where:
 *      - Keys are `Class` objects of types that extend `Event<?>`.
 *      - Values are `Callback<?>` implementations corresponding to the event type.
 *    - No invalid or null entries are allowed in the map.
 *    - For all keys added to `eventCallbacks`, the associated event type has been subscribed to via the `MessageBus` using the `MessageBusImpl.getInstance().subscribeEvent(type, this)` method call.
 *
 * 3. Field: `broadcastCallbacks`
 *    - The `broadcastCallbacks` map **always contains valid mappings** where:
 *      - Keys are `Class` objects of types that extend `Broadcast`.
 *      - Values are `Callback<?>` implementations corresponding to the broadcast type.
 *    - No invalid or null entries are allowed in the map.
 *    - For all keys added to `broadcastCallbacks`, the associated broadcast type has been subscribed to via the `MessageBus` using the `MessageBusImpl.getInstance().subscribeBroadcast(type, this)` method call.
 *
 * 4. Field: `name`
 *    - The field `name` is assigned a value exactly once at construction time and is never modified afterward.
 *    - It is guaranteed to be non-null throughout the lifetime of the `MicroService`.
 *
 * 5. Callback Consistency
 *    - For all received messages (events and broadcasts), if a message type exists in either the `eventCallbacks` or `broadcastCallbacks` map, its associated `Callback` will be invoked by the `run()` method.
 *    - No message type outside these maps can have a callback invoked.
 *    - The `Callback.call()` method is invoked in response to valid messages of types subscribed to by the microservice.
 *
 * 6. MessageBus Registration
 *    - Every instance of `MicroService` is **registered once** with the `MessageBus` singleton upon the start of its `run()` method.
 *    - The microservice is properly unregistered (via appropriate handling or termination) before the instance is discarded.
 *
 * 7. Subscription and Registry Consistency
 *    - Any event or broadcast type subscribed to (via `subscribeEvent` or `subscribeBroadcast`) is guaranteed to be registered with the `MessageBus`.
 *    - The `MessageBus` is informed of subscriptions before corresponding entries are added to `eventCallbacks` or `broadcastCallbacks`.
 *
 * 8. Lifecycle Management
 *    - The `MicroService`'s state reflects the following lifecycle:
 *      1. **Initialization Phase**:
 *         - The `initialize()` abstract method is called exactly once during the start of the `run()` method.
 *      2. **Active Phase**:
 *         - The event loop in `run()` must continue processing messages until `terminate()` sets `terminated` to `true`.
 *      3. **Termination Phase**:
 *         - No further messages are processed after `terminated` is set to `true`.
 *
 * 9. Thread Safety
 *    - The `eventCallbacks` and `broadcastCallbacks` maps are not exposed directly to any other object or thread.
 *    - Access or modifications to these fields are only allowed:
 *      - Via `subscribeEvent`.
 *      - Via `subscribeBroadcast`.
 *      - Through controlled internal operations in the `run()` loop.
 *
 * 10. Non-Blocking Behavior in Communication
 *     - The `sendEvent()` method returns a `Future<>` that might be resolved later by another service, and never blocks the execution of the calling microservice.
 *     - The `sendBroadcast()` method is non-blocking and immediately sends the broadcast message to all subscribers.
 */
public abstract class MicroService implements Runnable {
    private boolean terminated = false;
    private final String name;
    private  HashMap<Class<? extends Event<?>>, Callback<? extends Event<?>>> eventCallbacks; //@inv: eventcallbacks.get
    private  HashMap<Class<? extends Broadcast>, Callback<? extends Broadcast>> broadcastCallbacks;

    /**
     * @param name the micro-service name (used mainly for debugging purposes -
     *             does not have to be unique)
     */
    public MicroService(String name) {
        this.name = name;
        this.eventCallbacks = new HashMap<>();
        this.broadcastCallbacks = new HashMap<>();
    }

    /**
     * Subscribes to events of type {@code type} with the callback
     * {@code callback}. This means two things:
     * 1. Subscribe to events in the singleton event-bus using the supplied
     * {@code type}
     * 2. Store the {@code callback} so that when events of type {@code type}
     * are received it will be called.
     * <p>
     * For a received message {@code m} of type {@code type = m.getClass()}
     * calling the callback {@code callback} means running the method
     * {@link Callback#call(java.lang.Object)} by calling
     * {@code callback.call(m)}.
     * <p>
     * @param <E>      The type of event to subscribe to.
     * @param <T>      The type of result expected for the subscribed event.
     * @param type     The {@link Class} representing the type of event to
     *                 subscribe to.
     * @param callback The callback that should be called when messages of type
     *                 {@code type} are taken from this micro-service message
     *                 queue.
     */
    protected final <T, E extends Event<T>> void subscribeEvent(Class<E> type, Callback<E> callback) {
        MessageBusImpl.getInstance().subscribeEvent(type, this);
        eventCallbacks.put(type, callback);
    }

    /**
     * Subscribes to broadcast message of type {@code type} with the callback
     * {@code callback}. This means two things:
     * 1. Subscribe to broadcast messages in the singleton event-bus using the
     * supplied {@code type}
     * 2. Store the {@code callback} so that when broadcast messages of type
     * {@code type} received it will be called.
     * <p>
     * For a received message {@code m} of type {@code type = m.getClass()}
     * calling the callback {@code callback} means running the method
     * {@link Callback#call(java.lang.Object)} by calling
     * {@code callback.call(m)}.
     * <p>
     * @param <B>      The type of broadcast message to subscribe to
     * @param type     The {@link Class} representing the type of broadcast
     *                 message to subscribe to.
     * @param callback The callback that should be called when messages of type
     *                 {@code type} are taken from this micro-service message
     *                 queue.
     */
    protected final <B extends Broadcast> void subscribeBroadcast(Class<B> type, Callback<B> callback) {
        MessageBusImpl.getInstance().subscribeBroadcast(type, this);
        broadcastCallbacks.put(type, callback);
    }

    /**
     * Sends the event {@code e} using the message-bus and receive a {@link Future<T>}
     * object that may be resolved to hold a result. This method must be Non-Blocking since
     * there may be events which do not require any response and resolving.
     * <p>
     * @param <T>       The type of the expected result of the request
     *                  {@code e}
     * @param e         The event to send
     * @return  		{@link Future<T>} object that may be resolved later by a different
     *         			micro-service processing this event.
     * 	       			null in case no micro-service has subscribed to {@code e.getClass()}.
     */
    protected final <T> Future<T> sendEvent(Event<T> e) {
        return MessageBusImpl.getInstance().sendEvent(e);
    }

    /**
     * A Micro-Service calls this method in order to send the broadcast message {@code b} using the message-bus
     * to all the services subscribed to it.
     * <p>
     * @param b The broadcast message to send
     */
    protected final void sendBroadcast(Broadcast b) {
        MessageBusImpl.getInstance().sendBroadcast(b);
    }

    /**
     * Completes the received request {@code e} with the result {@code result}
     * using the message-bus.
     * <p>
     * @param <T>    The type of the expected result of the processed event
     *               {@code e}.
     * @param e      The event to complete.
     * @param result The result to resolve the relevant Future object.
     *               {@code e}.
     */
    protected final <T> void complete(Event<T> e, T result) {
        MessageBusImpl.getInstance().complete(e, result);
    }

    /**
     * this method is called once when the event loop starts.
     */
    protected abstract void initialize();

    /**
     * Signals the event loop that it must terminate after handling the current
     * message.
     */
    protected final void terminate() {
        this.terminated = true;
    }

    /**
     * @return the name of the service - the service name is given to it in the
     *         construction time and is used mainly for debugging purposes.
     */
    public final String getName() {
        return name;
    }

    /**
     * The entry point of the micro-service. TODO: you must complete this code
     * otherwise you will end up in an infinite loop.
     */
    @Override
    public final void run() {
        MessageBusImpl.getInstance().register(this);
        initialize();
        Message message;
        try {
            while (!terminated) {
                message = MessageBusImpl.getInstance().awaitMessage(this);
            //    System.out.println(name + " received " + message.getClass().getSimpleName());
                if (broadcastCallbacks.containsKey(message.getClass())) {
                    ((Callback<Broadcast>) broadcastCallbacks.get(message.getClass())).call((Broadcast)message);
                } else if (eventCallbacks.containsKey(message.getClass()))
                    ((Callback<Event<?>>) eventCallbacks.get(message.getClass())).call( (Event<?>)message);
            }
        }catch (InterruptedException e) {
         //       System.out.println("MicroService interrupted");
        }
     //   System.out.println("MicroService "+name+" terminated");
    }

    /**
     * Getters used ONLY for tests
     */
    public final int getEventSubSize(){
        return eventCallbacks.size();
    }
    public final int getBroadcastSubSize(){
        return broadcastCallbacks.size();
    }

}
