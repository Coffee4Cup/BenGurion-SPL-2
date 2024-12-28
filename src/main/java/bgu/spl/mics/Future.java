package bgu.spl.mics;

import java.util.concurrent.TimeUnit;

/**
 * A Future object represents a promised result - an object that will
 * eventually be resolved to hold a result of some operation. The class allows
 * Retrieving the result once it is available.
 * 
 * Only private methods may be added to this class.
 * No public constructor is allowed except for the empty constructor.
 */
public class Future<T> {
	private T result;
	
	/**
	 * This should be the only public constructor in this class.
	 */
	public Future() {
		//TODO: implement this
	}
	
	/**
     * retrieves the result the Future object holds if it has been resolved.
     * This is a blocking method! It waits for the computation in case it has
     * not been completed.
	 *
	 * @pre None.
	 * @post isDone() = true
	 *
     * <p>
     * @return return the result of type T if it is available, if not wait until it is available.
     * 	       
     */
	public T get() {
		while(!isDone());
		return result;
	}
	
	/**
     * Resolves the result of this Future object.
	 * @param {@value notnull} result
	 *
	 * @pre isDone() = false
	 * @post isDone() = true
     */
	public void resolve (T result) {
		this.result = result;
	}
	
	/**
     * @return true if this object has been resolved, false otherwise
	 * @pre None.
	 * @post result = @pre(result)
     */
	public boolean isDone() {
		return result != null;
	}
	
	/**
     * retrieves the result the Future object holds if it has been resolved,
     * This method is non-blocking, it has a limited amount of time determined
     * by {@code timeout}
     * <p>
     * @param timeout 	the maximal amount of time units to wait for the result.
     * @param unit		the {@link TimeUnit} time units to wait.
     * @return return the result of type T if it is available, if not, 
     * 	       wait for {@code timeout} TimeUnits {@code unit}. If time has
     *         elapsed, return null.
     */
	public T get(long timeout, TimeUnit unit) {
		//TODO: implement this.
		return null;
	}

}
