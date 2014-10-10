package org.ecnu.smartscore.task;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskPool extends Thread {

	private static TaskPool mInst = null;
	private Logger log;
	private final static long waitTime = 60*10*1000;    // 10 minutes
	private volatile boolean mRunning;

	private ConcurrentLinkedQueue<Runnable> mThreadPool;

	public static TaskPool getInstance() {
		if (mInst  == null) {
			mInst = new TaskPool();
		}
		return mInst;
	}
	
	private TaskPool() {
		mThreadPool = new ConcurrentLinkedQueue<Runnable>();
		mRunning = true;
		log = LoggerFactory.getLogger(this.getClass());
	}
	
	public void put(TaskItem th) {
		mThreadPool.add(th);
	}
	
	public boolean terminate() {
		mRunning = false;
		try {
			this.join(6000);
		} catch (InterruptedException e) {
			log.error("Terminate error.");
			return false;
		}
		return true;
	}
	
	@Override
	public void run() {
		try {
			while (mRunning) {
				if (mThreadPool.size() == 0) {
					Thread.sleep(5000);
					log.info("Sleeping.");
				} else {
					log.info("Executing thread.");
					Runnable runnable = mThreadPool.poll();
					if (runnable == null) {
						log.error("Retrieved null task");
					} else {
						Thread th = new Thread(runnable);
						long startTime = System.currentTimeMillis();
						th.run();
						try {
							th.join(waitTime);
							long endTime = System.currentTimeMillis();
							log.info(String.format("Task completed in %d ms.", (endTime - startTime)));
						} catch (InterruptedException e) {
							log.error("Task interrupted due to overtiming.");
						}
					}
				}
			}
		} catch (InterruptedException e) {
			log.trace("Exits.");
		}
	}

}
