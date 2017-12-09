package dev.tunks.buffer;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Before;
import org.junit.Test;

public class DataBufferDataStreamTest  {
    private  DataBufferDataStream<String> bufferStream;
    private  Queue<String> inputStream;
    private CountDownLatch latch  = new CountDownLatch(2);
    private ExecutorService executor = Executors.newFixedThreadPool(2);
    
    @Before
	public void setUp() {
		inputStream = new ConcurrentLinkedDeque();
		bufferStream = new DataBufferDataStream(inputStream);
	}
	
    @Test
	public void testGet() throws InterruptedException {
		List<String> results = new ArrayList();
		executor.execute(new BufferTask(results));
		{
		assertTrue(results.size() == 0);	
		}
		
		{
			insertElements("1",5);
			latch.await(10, TimeUnit.SECONDS);
		    System.out.println("-------------------------------------------------------------------------------");
			System.out.println("1. Results "+results.size());
			assertTrue(results.size() >0);	
		}
		
		{
		    results.clear();
		    System.out.println("--------------------------------------------------------------------------------");
			System.out.println("1.1 Results "+results.size());
		    assertTrue(results.size() == 0);	
		}
		
		{
			insertElements("2",5);
			bufferStream.update(null,Boolean.TRUE);
			latch.await(10, TimeUnit.SECONDS);
		    System.out.println("---------------------------------------------------------------------------------");
			System.out.println("2: Results "+results.size());
			assertTrue(results.size() >0);	
		}
		
		executor.shutdown();

	}

	public void tearDown() throws Exception {
	}
	
	private void insertElements(String prefixId, int size) {
		for(int i =0 ;i < size; i++) {
			insertElement(prefixId, i);
		}
	}
	
	private void insertElement(String prefixId, int index) {
		inputStream.add("Alarm{i: " + prefixId+"_"+ index+ "}");
	}
	
	private String getElement() {
		return bufferStream.get();	
	}
	private class BufferTask implements Runnable{
		private List results;
		private AtomicBoolean paused = new AtomicBoolean(false);
		public BufferTask(List results) {
			this.results = results;
		}

		//public void toggle()
		public void run() {
                while(true) {
                	  String ele = DataBufferDataStreamTest.this.getElement();
                	  if(ele != null) {
                		  results.add(ele);
                	  }
                	  	
              }
		}

	}

}
