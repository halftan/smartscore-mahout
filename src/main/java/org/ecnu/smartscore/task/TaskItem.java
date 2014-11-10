package org.ecnu.smartscore.task;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskItem implements Runnable {

	private String mOption;
	private static Logger log;
	static {
		log = LoggerFactory.getLogger("TaskItem");
	}
	
	public TaskItem(String option) {
		mOption = option;
	}
	
	public Thread dispatch() {
		Thread th = new Thread(this, "Task " + mOption);
		return th;
	}

	@Override
	public void run() {
		switch (mOption) {
		case "bye":
			log.info("Quit.");
			break;
		case "kmeans":
			log.info("Running kmeans example case.");
			invokeRunner("KmeansRunner");
			log.info("Task K-Means finished successfully.");
			break;
		case "fuzzyk":
			log.info("Runninng fuzzy kmeans example case.");
			invokeRunner("FuzzyKmeansRunner");
			log.info("Task Fuzzy K-Means finished successfully.");
			break;
		default:
			log.error("Not recognized.");
			break;
		}
	}
	
	private void invokeRunner(String className) {
		try {
			ClassLoader loader = this.getClass().getClassLoader();
			Class<?> runner = loader.loadClass("org.ecnu.smartscore.clustering.runner."+className);
			Method runMethod = runner.getMethod("run");
			runMethod.invoke(null);
		} catch (ClassNotFoundException e) {
			log.error("Required class org.ecnu.smartscore.runner."+className+" not loaded!");
		} catch (Exception e) {
			log.error("Unknwon error in task runner "+className, e);
		}
	}
}
