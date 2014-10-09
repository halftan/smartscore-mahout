package smartscore.task;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.LoggerFactory;

public class TaskPool extends Thread {

	private static TaskPool mInst = null;

	private ConcurrentLinkedQueue<Thread> mThreadPool;

	public static TaskPool getInstance() {
		if (mInst  == null) {
			mInst = new TaskPool();
		}
		return mInst;
	}
	
	private TaskPool() {
		mThreadPool = new ConcurrentLinkedQueue<Thread>();
	}
	
	@Override
	public void run() {
		try {
			Thread.sleep(5000);
			LoggerFactory.getLogger(this.getClass().getName()).info("Notified.");
		} catch (InterruptedException e) {
			LoggerFactory.getLogger(this.getClass().getName()).info("Exits.");
		}
	}

}
