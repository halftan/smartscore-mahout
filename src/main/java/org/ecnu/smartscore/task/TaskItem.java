package org.ecnu.smartscore.task;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskItem implements Runnable {

	private TaskOption option;
	private static Logger log = LoggerFactory.getLogger(TaskItem.class);

	public TaskItem(TaskOption option) {
		this.option = option;

		log.info("Task created with options: {}", option);
	}

	@Override
	public void run() {
		switch (option.getRunner()) {
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
			Class<?> runner = loader
					.loadClass("org.ecnu.smartscore.clustering.runner."
							+ className);
			Method runMethod = runner.getMethod("run");

			log.info("Executing task.");

			try {
				long startTime = System.currentTimeMillis();
				runMethod.invoke(null);
				long endTime = System.currentTimeMillis();
				log.info(String.format("Task completed in %d ms.",
						(endTime - startTime)));
			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (ClassNotFoundException e) {
			log.error("Required class org.ecnu.smartscore.runner." + className
					+ " not loaded!");
		} catch (Exception e) {
			log.error("Unknwon error in task runner " + className, e);
		}
	}
}
