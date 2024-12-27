package bgu.spl.mics;

import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.services.CameraService;
import bgu.spl.mics.application.services.TimeService;
import bgu.spl.mics.example.services.ExampleBroadcastListenerService;
import bgu.spl.mics.example.services.ExampleEventHandlerService;
import bgu.spl.mics.example.services.ExampleMessageSenderService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class TestMessageBus{
	private MessageBusImpl msgBus;
	private MicroService m1,m2;
	
	@BeforeEach
	public void setUp() {
		Camera c = new Camera();
		m1 = new CameraService(c);
		m2 = new TimeService(1000 , 10);
	}
	
	@AfterEach
	public void tearDown() {
		
	}
	
	@Test
	public void testSomething() {
		Thread t1 = new Thread(m1);
		Thread t2 = new Thread(m2);
		t1.start();
		t2.start();
	}
}
