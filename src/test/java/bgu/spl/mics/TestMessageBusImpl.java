package bgu.spl.mics;

import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.services.TimeService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class TestMessageBusImpl {
	MessageBusImpl messageBus;
	MicroService microService;
	MicroService microService2;
	LinkedList<Future<String>> futures;
	LinkedList<Thread> threadList;
	LinkedList<MicroService> testServices;

	Event dummyEvent;

	private class DummyEvent implements Event<String> {
		private Future<String> dummyFuture;
		String dummyString;

		public DummyEvent(String query) {
			dummyString = query;
			dummyFuture = new Future<>();
		}

		public String getDummyString() {
			return dummyString;
		}

		@Override
		public Future<String> getFuture() {
			return dummyFuture;
		}
		// Implement any necessary methods or fields for the DummyEvent class
	}

	private class DummyBroadcast implements Broadcast {
		private int recievedCounter;

		public DummyBroadcast() {
			recievedCounter = 0;
		}

		public void recieved() {
			recievedCounter++;
		}

		public int getAmountRecieved() {
			return recievedCounter;
		}
	}

	@BeforeEach
	public void setUp() {
		futures = new LinkedList<>();
		testServices = new LinkedList<>();
		threadList = new LinkedList<>();
		for (int i = 1; i <= 10; i++) {
			testServices.add(new MicroService("TestService" + i) {
				private String message;

				public void setMessage(String message) {
					this.message = message;
				}

				public String getMessage() {
					return message;
				}

				@Override
				protected void initialize() {
					subscribeEvent(DummyEvent.class, (event) -> {
						if (event.getDummyString().equals("Query"))
							complete(event, "response by " + getName());
					});
					subscribeBroadcast(DummyBroadcast.class, (t) -> t.recieved());
					subscribeBroadcast(TerminatedBroadcast.class, (t) -> terminate());
				}
			});

			threadList.add(new Thread(testServices.getLast()));
		}

		microService = new MicroService("Test") {
			@Override
			protected void initialize() {
				subscribeEvent(DummyEvent.class, (event) -> {
					if (event.getDummyString().equals("Query"))
						complete(event, "response");
				});
				subscribeBroadcast(TerminatedBroadcast.class, (t) -> terminate());
			}
		};
		dummyEvent = new DummyEvent("Query");
	}

	/**
	 *
	 */
	

	
	@AfterEach
	public void tearDown() {
		
	}
	@Test
	public void testRegister(){
		for(MicroService m: testServices){
			MessageBusImpl.getInstance().register(m);
		}/**
		try{
			Thread.sleep(1000);
			microService.sendBroadcast(new TerminatedBroadcast());
			for(Thread t: threadList){
				t.join();
			}
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}*/
        assertEquals(10, (int) MessageBusImpl.getInstance().getBusSize());
	}

	@Test
	public void testSubscribeBroadcast() {
		for(MicroService m : testServices){
			m.subscribeBroadcast(TerminatedBroadcast.class, (t) -> m.terminate());
		}/**
		try{
			Thread.sleep(1000);
			microService.sendBroadcast(new TerminatedBroadcast());
			for(Thread t: threadList){
				t.join();
			}
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}*/
       // assertEquals(10, (int) MessageBusImpl.getInstance().getBroadcastSubsSize(DummyBroadcast.class));
        assertEquals(10, (int) MessageBusImpl.getInstance().getBroadcastSubsSize(TerminatedBroadcast.class));

	}
	@Test
	public void testSubscribeEvent() {
		for(MicroService m : testServices){
			m.subscribeEvent(DummyEvent.class, (event) -> {
				if (event.getDummyString().equals("Query"))
					m.complete(event, "response by " + m.getName());
			});
		}/**
		try{
			Thread.sleep(1000);
			microService.sendBroadcast(new TerminatedBroadcast());
			for(Thread t: threadList){
				t.join();
			}
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}*/
        assertEquals(10, (int) MessageBusImpl.getInstance().getEventSubsSize(DummyEvent.class));

	}/**
	@Test
	public void testComplete() {

	}
	@Test
	public void testSendEvent(){

	}
	@Test
	public void testSendBroadcast() {

	}

	@Test
	public void testSendTerminateBroadcast() {
		for(Thread t: threadList){
			t.start();
		}
		MessageBusImpl.getInstance().sendBroadcast(new TerminatedBroadcast());
        assertEquals(0, (int) MessageBusImpl.getInstance().getBusSize());
	}

	@Test
	public void testUnregister(){

	}
	@Test
	public void testAwaitMessage(){

	}*/
}
