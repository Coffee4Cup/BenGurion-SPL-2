package bgu.spl.mics;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.TimeUnit;
public class TestFuture {
	private Future<Integer> f;
	private int i2;
	
	@BeforeEach
	public void setUp() {
		f = new Future<Integer>();
	}
	
	@AfterEach
	public void tearDown() {
		
	}
	/**
	 * @param T result
	 * @PRE non
	 * @POST isDone() returns true
	 * @POST result != null
	 * 
	 * @returns result if and only if result != null
	 * blocks current thread until result != null
	 *
	@Test
	public void testGet() {
		Thread t1 = new Thread(()->  i2 = f.get());
		t1.start();
		f.resolve(1);
		try {
			t1.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(i2 == 1);
	}
	/**
	 * @param boolean
	 * @PRE non
	 * @POST non
	 * 
	 * @returns result != null
	 *
	 *
	@Test
	public void testIsDone() {
		assertTrue(!f.isDone());
		f.resolve(1);
		assertTrue(f.isDone());
	}
	
	/**
	 * @param void
	 * @PRE result == null
	 * @POST result != null
	 * notifies all waiting on result after update
	 * 
	 * 
	 *
	@Test
	public void testResolve() {
		Thread t1 = new Thread(()->i2 = f.get());
		Thread t2 = new Thread(()->i2 = f.get());
		Thread t3 = new Thread(()->i2 = f.get());
		t1.start();
		t2.start();
		t3.start();
		f.resolve(1);
		try {
			t1.join();
			t2.join();
			t3.join();
		} catch (InterruptedException e) {
			assertTrue(false);
			e.printStackTrace();
		}
		assertTrue(i2 == 1);
	}
	
	/**
	 * @param T result
	 * @PRE non
	 * @POST non
	 *
	@Test
	public void testGetWithTimeout() {
		TimeUnit unit = TimeUnit.MICROSECONDS;
		Thread t1 = new Thread(()->f.get(1, unit)); //how do you handle null? currently errors.
		t1.start();
		try {
			t1.join();
		} catch (InterruptedException e) {
			Assertions.fail();
		}
		assertTrue(!f.isDone());
		f.resolve(1);
		i2 = f.get(1, unit);
		assertTrue(i2 == 1);
	}
	*/
}
