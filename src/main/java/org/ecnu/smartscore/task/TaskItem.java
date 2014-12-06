package org.ecnu.smartscore.task;

import java.lang.reflect.Method;
import java.sql.SQLException;

import org.ecnu.smartscore.dao.DAOFactory;
import org.ecnu.smartscore.dao.IComputeTaskDAO;
import org.ecnu.smartscore.po.ComputeTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskItem implements Runnable {

	private TaskOption option;
	private final static Logger LOGGER = LoggerFactory
			.getLogger(TaskItem.class);

	public TaskItem(TaskOption option) {
		this.option = option;

		LOGGER.info("Task created with options: {}", option);
	}

	@Override
	public void run() {
		IComputeTaskDAO dao = null;
		try {
			dao = DAOFactory.getComputeTaskDAOInstance();
		} catch (SQLException e) {
			LOGGER.error("Cannot get DAO instance", e);
		}

		ComputeTask task = dao.getComputeTaskByTaskId(option.getTaskId());
		dao.close();

		if (task == null) {
			LOGGER.warn("Task #{} not found!", option.getTaskId());
			return;
		}

		switch (task.getRunner()) {
		case "kmeans":
			LOGGER.info("Running kmeans example case.");
			invokeRunner("KmeansRunner");
			LOGGER.info("Task K-Means finished successfully.");
			break;
		case "fuzzyk":
			LOGGER.info("Runninng fuzzy kmeans example case.");
			invokeRunner("FuzzyKmeansRunner");
			LOGGER.info("Task Fuzzy K-Means finished successfully.");
			break;
		default:
			LOGGER.error("Not recognized.");
			return;
		}

		// Send mail

	}

	private void invokeRunner(String className) {
		try {
			ClassLoader loader = this.getClass().getClassLoader();
			Class<?> runner = loader
					.loadClass("org.ecnu.smartscore.clustering.runner."
							+ className);
			Method runMethod = runner.getMethod("run", String.class,
					String.class);

			LOGGER.info("Executing task.");

			try {
				long startTime = System.currentTimeMillis();
				runMethod.invoke(null, null, null);
				long endTime = System.currentTimeMillis();
				LOGGER.info(String.format("Task completed in %d ms.",
						(endTime - startTime)));
			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (ClassNotFoundException e) {
			LOGGER.error("Required class org.ecnu.smartscore.runner."
					+ className + " not loaded!");
		} catch (Exception e) {
			LOGGER.error("Unknwon error in task runner " + className, e);
		}
	}
}
