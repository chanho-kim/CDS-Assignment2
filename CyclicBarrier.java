import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class CyclicBarrier {
	
	Semaphore s1;
	Semaphore s2;
	int limit;
	AtomicInteger waiting;
		
	public CyclicBarrier(int parties){
		s1 = new Semaphore(parties);
		s2 = new Semaphore(parties);
		try {
			s2.acquire(parties);
		} catch (InterruptedException e) {
		}
		limit = parties;
		waiting.set(parties);
	}
	
	int await() throws InterruptedException {
		int index;
		
		//prevent any more threads from entering
		s1.acquire();
		
		//decrement the counter & set the index for return value
		index = waiting.decrementAndGet();
		
		//if enough threads have entered, release them and reset counter, then acquire one for this thread
		if(waiting.get() == 0) {
			waiting.set(limit);
			s2.release(limit);
			s2.acquire();
		}
		
		//all the threads that came before should be stuck here until the last thread releases
		else s2.acquire();
		
		//release all semaphores upon exit
		s1.release();
		s2.release();
		
		return index;
	}
	
}
