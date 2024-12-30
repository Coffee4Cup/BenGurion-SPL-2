package bgu.spl.mics;

import bgu.spl.mics.application.messages.TerminatedBroadcast;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestReporter;

import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;

class TestMicroService {
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
    void setUp() {
        futures = new LinkedList<>();
        testServices = new LinkedList<>();
        threadList = new LinkedList<>();
        for(int i=1;i<=10;i++){
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
                        if(event.getDummyString().equals("Query"))
                            complete(event, "response by "+getName());
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
                    if(event.getDummyString().equals( "Query"))
                        complete(event, "response");
                });
                subscribeBroadcast(TerminatedBroadcast.class, (t) -> terminate());
            }
        };
        dummyEvent = new DummyEvent("Query");


    }

    @Test
    void complete() {
        Thread testThread1 = new Thread(microService);
        testThread1.start();
        Future<String> future = microService.sendEvent(dummyEvent);
        microService.sendBroadcast(new TerminatedBroadcast());
        try{
            testThread1.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        assertNotNull(future);
        assertEquals("response", future.get());
        System.out.println(future.get());
    }

    @Test
    void subscribeEvent() {for(Thread t: threadList){
        t.start();
    }
        try{
            Thread.sleep(1000);
            microService.sendBroadcast(new TerminatedBroadcast());
            for(Thread t: threadList){
                t.join();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        for(MicroService m: testServices){
            assertTrue(m.getEventSubSize() == 1);
        }

    }

    @Test
    void subscribeBroadcast() {
        for(Thread t: threadList){
            t.start();
        }
        try{
            Thread.sleep(1000);
            microService.sendBroadcast(new TerminatedBroadcast());
            for(Thread t: threadList){
                t.join();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        for(MicroService m: testServices){
            assertTrue(m.getBroadcastSubSize() == 2);
        }
    }

    @Test
    void sendEvent() {
        for(Thread t: threadList){
            t.start();
        }
        try{
            //Thread.sleep(1000);
            for(int i=1;i<=100;i++){
                futures.add(microService.sendEvent(dummyEvent));
            }
            microService.sendBroadcast(new TerminatedBroadcast());
            for(Thread t: threadList){
                t.join();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        for(Future<String> f: futures){
            assertTrue(f.get().substring(0,8).equals("response"));
        }
    }

    @Test
    void sendBroadcast() {
        DummyBroadcast dummyBroadcast = new DummyBroadcast();
        for(Thread t: threadList){
            t.start();
        }
        try{
            Thread.sleep(1000);
            for(int i=1;i<=3;i++){
                microService.sendBroadcast(dummyBroadcast);
            }
            microService.sendBroadcast(new TerminatedBroadcast());
            for(Thread t: threadList){
                t.join();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        assertTrue(dummyBroadcast.getAmountRecieved() == 30);
    }

    @Test
    void initialize() {

    }

    @Test
    void terminate() {
    }

    @Test
    void getName() {
    }

    @Test
    void run() {
    }
}