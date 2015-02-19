import java.util.concurrent.atomic.AtomicInteger;

public class FairReadWriteLock {
	
	AtomicInteger reader = new AtomicInteger(0);
	AtomicInteger writer = new AtomicInteger(0);
	
	public FairReadWriteLock(){
	}
	
	public synchronized void beginRead() {
		while(writer.get() > 0) {
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}
		reader.incrementAndGet();
	}
	
	public synchronized void endRead() {
		reader.decrementAndGet();
		notifyAll();
	}
	
	public synchronized void beginWrite() {
		while(reader.get() > 0 || writer.get() > 0) {
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}
		writer.incrementAndGet();
	}
	
	public synchronized void endWrite() {
		writer.decrementAndGet();
		notifyAll();
	}
}
