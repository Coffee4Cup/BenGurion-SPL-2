package bgu.spl.mics;

/*
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only one public method (in addition to getters which can be public solely for unit testing) may be added to this class
 * All other methods and members you add the class must be private.
 */

import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;
import bgu.spl.mics.application.objects.EventRoundRobin;


/**
 * order of sync:
 * eventSubscriptions
 * event sub list
 * broadcastSubscriptions
 * broadcast sub list
 * bus
 * message queue
 * eventRoundRobin
 * EventRoundRobin class
 */
public class MessageBusImpl implements MessageBus {




	private final HashMap<MicroService, LinkedBlockingQueue<Message>> bus;
	private final HashMap<Class<? extends Event<?>>, LinkedList<MicroService>> eventSubscriptions;
	private final HashMap<Class<? extends Broadcast>, LinkedList<MicroService>> broadcastSubscriptions;
	private final HashMap<Class<? extends Event<?>>, EventRoundRobin> eventRoundRobin;

	private static class MessageBusHolder{
		private static final MessageBusImpl instance = new MessageBusImpl();
	}

	public static MessageBusImpl getInstance(){
		return MessageBusImpl.MessageBusHolder.instance;
	}

	private MessageBusImpl(){
	//	System.out.println("Initializing MessageBusImpl");
		bus = new HashMap<MicroService, LinkedBlockingQueue<Message>>();
		eventSubscriptions = new HashMap<Class<? extends Event<?>>, LinkedList<MicroService>>();
		broadcastSubscriptions = new HashMap<Class<? extends Broadcast>, LinkedList<MicroService>>();
		eventRoundRobin = new HashMap<Class<? extends Event<?>>, EventRoundRobin>();
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
	//	System.out.println(m.getName() + " subscribed to ");
		LinkedList<MicroService> eventSubs;
		synchronized(eventSubscriptions) {
			//get list of ms subbed to event
			//if nobody is subbed, create new list of subs
			eventSubs = eventSubscriptions.computeIfAbsent(type, k -> new LinkedList<MicroService>());
			synchronized(eventSubs) {
				eventSubs.add(m);
				synchronized (eventRoundRobin){
					if(!eventRoundRobin.containsKey(type)) {
						eventRoundRobin.put(type, new EventRoundRobin(1));
					}
					else{
						eventRoundRobin.get(type).sizeIncrement();
					}
				}
			}
			eventSubscriptions.notifyAll();
		}


	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
	//	System.out.println(m.getName() + " subscribing to ");
		LinkedList<MicroService> broadcastSubs;
		synchronized(broadcastSubscriptions) {
			//get list of ms subbed to broadcast
			//if nobody is subbed, create new list of subs
			broadcastSubs = broadcastSubscriptions.computeIfAbsent(type, k -> new LinkedList<MicroService>());
			synchronized(broadcastSubs) {
				broadcastSubs.add(m);
			}
		}

	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		e.getFuture().resolve(result);
	}

	@Override
	public void sendBroadcast(Broadcast b) {
	//	System.out.println("Sending Broadcast");
		LinkedList<MicroService> subs;
		synchronized(broadcastSubscriptions) {
			subs = broadcastSubscriptions.get(b.getClass());
		}
		synchronized(subs) {
			for(MicroService m : subs) {
				synchronized(bus) {
					bus.get(m).offer(b);
				}
			}
		}

	}

	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
	//	System.out.println("Sending event: " + e);
		LinkedList<MicroService> subs;
		synchronized(eventSubscriptions) {
			//get list of ms subbed to event
			subs = eventSubscriptions.get(e.getClass());
			while (subs == null) {
				try {
					eventSubscriptions.wait();
					subs = eventSubscriptions.get(e.getClass());
				}catch (InterruptedException ie){
					subs = eventSubscriptions.get(e.getClass());
					if(subs != null){
						break;
					}
					continue;
				}
			}
			synchronized (subs) {
				MicroService m;
				synchronized (eventRoundRobin) {
					m = subs.get(eventRoundRobin.get(e.getClass()).indexIncrement());
				}
				synchronized (bus) {
					bus.get(m).offer(e);
				}
			}
		}
		return e.getFuture();
	}

	@Override
	public void register(MicroService m) {
	//	System.out.println(m.getName() + " registered");
		synchronized(bus){
			bus.put(m, new LinkedBlockingQueue<Message>());			//when do i sub? NOW!
		}
	}

	@Override
	public void unregister(MicroService m) {
		// TODO Auto-generated method stub

	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
	//	System.out.println(m.getName() + " awaiting message");
		LinkedBlockingQueue<Message> queue;
		synchronized(bus) {			//TODO check if necessary to lock here
			queue = bus.get(m);
		}
		return queue.take();
	}






	/**
	 * Getters used ONLY for tests
	 */
	public final Integer getBusSize(){
		if(bus == null)
			return null;
		return bus.size();
	}
	public final Integer getQueueSize(MicroService m){
		if(!bus.containsKey(m)){
			return null;
		}
		return bus.get(m).size();
	}
	public final Integer getNumOfEvents(){
		if(eventSubscriptions == null)
			return null;
		return eventSubscriptions.size();
	}
	public final Integer getNumOfBroadcasts(){
		if(broadcastSubscriptions == null)
			return null;
		return broadcastSubscriptions.size();
	}
	public final Integer getEventSubsSize(Class<? extends Event<?>> clazz){
		if(!eventSubscriptions.containsKey(clazz))
			return null;
		return eventSubscriptions.get(clazz).size();
	}
	public final Integer getBroadcastSubsSize(Class<? extends Broadcast> clazz){
		if(!broadcastSubscriptions.containsKey(clazz))
			return null;
		return broadcastSubscriptions.get(clazz).size();
	}
	public final Integer getTotalRoundRobins(){
		if(eventRoundRobin == null)
			return null;
		return eventRoundRobin.size();
	}
	public final Integer getRoundRobinSubsSize(Class<? extends Event<?>> clazz){
		if(!eventRoundRobin.containsKey(clazz))
			return null;
		return eventRoundRobin.get(clazz).getCurrentSize();
	}
	public final Integer getRoundRobinIndex(Class<? extends Event<?>> clazz){
		if(eventRoundRobin == null)
			return null;
		return eventRoundRobin.get(clazz).getIndex();
	}


}
