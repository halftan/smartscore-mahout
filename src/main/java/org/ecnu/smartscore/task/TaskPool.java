package org.ecnu.smartscore.task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskPool {

	private static TaskPool selfInstance = null;

	private ExecutorService threadPool = null;

	private Logger log = LoggerFactory.getLogger(TaskPool.class);

	public static TaskPool getInstance() {
		if (selfInstance == null) {
			selfInstance = new TaskPool();
		}
		return selfInstance;
	}

	private TaskPool() {
		threadPool = Executors.newCachedThreadPool();
	}

	public void put(Runnable th) {
		log.info("Task received in task pool.");
		threadPool.execute(th);
	}

	public void terminate() {
		threadPool.shutdown();
	}
}
