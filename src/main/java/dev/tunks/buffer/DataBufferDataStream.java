package dev.tunks.buffer;

import java.util.Observable;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DataBufferDataStream<T> implements BaseDataStream<T> {
	private ReentrantLock lock = new ReentrantLock();
	private Condition empty = lock.newCondition();
	private Condition notFull = lock.newCondition();

	@SuppressWarnings("unused")
	private BlockingQueue<T> buffer = new  LinkedBlockingDeque<T>();

	private int capacity = 20;
	private long waitTimeInMilliSeconds = 1000;
	private Queue<T> inputStream;

	public DataBufferDataStream(Queue<T> inputStream) {
		this.inputStream = inputStream;
	}

	public  T get() {
		 populateBufferQueue();
		try {
			return getElement();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	private T getElement() throws InterruptedException {
		lock.lock();
		try {
			//System.out.println("Get element : size => "+buffer.size());
			T element = buffer.poll(waitTimeInMilliSeconds,TimeUnit.MILLISECONDS);
			notFull.signal();
			//System.out.println("Get element : "+element);
			return element;
		} finally {
		   lock.unlock();	
		}
	}

	public void remove(T object) {
		// TODO Auto-generated method stub
       System.out.println("Remove from processing queue: "+object);
	}

	public void update(Observable o, Object arg) {
        System.out.println("Updated");
		populateBufferQueue();
	}

	private void populateBufferQueue() {
		lock.lock();
		try {
 			//System.out.println("Populating buffer :"+buffer.size());
             if(buffer.isEmpty()) {
            	    int i =0;
            	    T item;
            	    boolean isEmpty = false;
            	    do {
            	     item = inputStream.poll();
          	    	  if(item != null) {
          	    		  buffer.offer(item);//, 10, TimeUnit.SECONDS);
          	    	  }
          	    	  else {
          	    		isEmpty = true;
          	    	  }
          	    	  i=+1;
            	    }while(i< capacity && !isEmpty);
         	  //System.out.println("Populated buffer done :"+buffer.size());
             }
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			lock.unlock();
		}
	}

}
