import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class Garden {
	
	Lock lock = new ReentrantLock();
	Condition holeExists = lock.newCondition();
	Condition unseededHole = lock.newCondition();
	Condition tooManyHoles = lock.newCondition();
	Condition shovelAvailable = lock.newCondition();
		
	AtomicInteger emptyHoles = new AtomicInteger(0);
	AtomicInteger seededHoles = new AtomicInteger(0);
	AtomicBoolean shovel = new AtomicBoolean(true);
	
	int max;
	
	public Garden(int MAX) {
		max = MAX;
	}
	
	public void startDigging() {
		while(seededHoles.get() == max){
			try {
				tooManyHoles.await();
			} catch (InterruptedException e) {
			}
		}
		while(!shovel.get()){
			try {
				shovelAvailable.await();
			} catch (InterruptedException e) {
			}
		}
	}
	
	public void doneDigging() {
		emptyHoles.incrementAndGet();
		holeExists.signal();
		shovelAvailable.signal();
	}
	
	public void startSeeding() {
		while(emptyHoles.get() == 0) {
			try {
				holeExists.await();
			} catch (InterruptedException e) {
			}
		}
	}
	
	public void doneSeeding() {
		emptyHoles.decrementAndGet();
		seededHoles.incrementAndGet();
		unseededHole.signal();
	}
	
	public void startFilling() {
		while(seededHoles.get() == 0){
			try {
				unseededHole.await();
			} catch (InterruptedException e) {
			}
		}
		while(!shovel.get()) {
			try {
				shovelAvailable.await();
			} catch (InterruptedException e) {
			}
		}
	}
	
	public void doneFilling() {
		seededHoles.decrementAndGet();
		tooManyHoles.signal();
		shovelAvailable.signal();
	}
	
}
