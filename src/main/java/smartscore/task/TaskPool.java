package smartscore.task;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskPool extends Thread {

	private static TaskPool mInst = null;
	private Logger Logger;
	private final static long waitTime = 60*10*1000;    // 10 minutes

	private ConcurrentLinkedQueue<Runnable> mThreadPool;

	public static TaskPool getInstance() {
		if (mInst  == null) {
			mInst = new TaskPool();
		}
		return mInst;
	}
	
	private TaskPool() {
		mThreadPool = new ConcurrentLinkedQueue<Runnable>();
		Logger = LoggerFactory.getLogger(this.getClass());
	}
	
	public void put(TaskItem th) {
		mThreadPool.add(th);
	}
	
	@Override
	public void run() {
		try {
			while (true) {
				if (mThreadPool.size() == 0) {
					Thread.sleep(5000);
					Logger.info("Sleeping.");
				} else {
					Logger.info("Executing thread.");
					Runnable runnable = mThreadPool.poll();
					if (runnable == null) {
						Logger.error("Retrieved null task");
					} else {
						Thread th = new Thread(runnable);
						long startTime = System.currentTimeMillis();
						th.run();
						try {
							th.join(waitTime);
							long endTime = System.currentTimeMillis();
							Logger.info(String.format("Task completed in %d ms.", (endTime - startTime)));
						} catch (InterruptedException e) {
							Logger.error("Task interrupted due to overtiming.");
						}
					}
				}
			}
		} catch (InterruptedException e) {
			Logger.trace("Exits.");
		}
	}

}
