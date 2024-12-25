package bgu.spl.mics;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class TestMessageBus{
	private MessageBus msgBus;
	private Integer i1,i2;
	
	@BeforeEach
	public void setUp() {
		msgBus = new MessageBusImpl();
		i1 = 1;
		i2 = 1;
	}
	
	@AfterEach
	public void tearDown() {
		
	}
	
	@Test
	public void testSomething() {
		assertTrue(i1 == i2);
	}
	@Test
	public void testAnother() {
		assertTrue(i1 == i2);
	}
	@Test
	public void testSomeMore() {
		assertTrue(i1 == i2);
	}
}
