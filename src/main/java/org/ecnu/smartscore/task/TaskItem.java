package org.ecnu.smartscore.task;

import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.SQLException;

import org.ecnu.smartscore.dao.DAOFactory;
import org.ecnu.smartscore.dao.IComputeTaskDAO;
import org.ecnu.smartscore.mail.MailBuilder;
import org.ecnu.smartscore.mail.MailService;
import org.ecnu.smartscore.po.ComputeTask;
import org.ecnu.smartscore.utils.FilePath;
import org.ecnu.smartscore.utils.ZipCompress;
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

		String inputPath = FilePath.getFullInputPath(task.getInputPath());
		String outputPath = FilePath.getFullOutputPath(task.getOutputPath());

		switch (task.getRunner()) {
		case "kmeans":
			LOGGER.info("Running kmeans example case.");
			invokeRunner("KmeansRunner", inputPath, outputPath);
			LOGGER.info("Task K-Means finished successfully.");
			break;
		case "fuzzyk":
			LOGGER.info("Runninng fuzzy kmeans example case.");
			invokeRunner("FuzzyKmeansRunner", inputPath, outputPath);
			LOGGER.info("Task Fuzzy K-Means finished successfully.");
			break;
		default:
			LOGGER.error("Not recognized.");
			return;
		}

		// Send mail
		try {
			MailService.getInstance().sendMail(task.getEmail(),
					MailBuilder.buildHtmlMail(option));
		} catch (IOException e) {
			LOGGER.error("Cannot send mail", e);
		}
	}

	private void invokeRunner(String className, String inputPath,
			String outputPath) {
		try {
			ClassLoader loader = this.getClass().getClassLoader();
			Class<?> runner = loader
					.loadClass("org.ecnu.smartscore.clustering.runner."
							+ className);
			Method runMethod = runner.getMethod("run", String.class,
					String.class);

			IComputeTaskDAO dao = DAOFactory.getComputeTaskDAOInstance();
			dao.updateComputeTaskStateByTaskId(option.getTaskId(),
					IComputeTaskDAO.STATE_RUNNING);
			dao.close();
			dao = null;

			LOGGER.info("Executing task...");

			boolean finished = false;
			try {
				long startTime = System.currentTimeMillis();
				runMethod.invoke(null, inputPath, outputPath);
				long endTime = System.currentTimeMillis();
				LOGGER.info("Task completed in {} ms.", endTime - startTime);

				LOGGER.info("Compressing result...");
				startTime = System.currentTimeMillis();
				ZipCompress.compressDir(outputPath);
				endTime = System.currentTimeMillis();
				LOGGER.info("Result compressed in {} ms", endTime - startTime);

				finished = true;
			} catch (Exception e) {
				LOGGER.error("Compute error in task runner", e);
			}

			dao = DAOFactory.getComputeTaskDAOInstance();
			dao.updateComputeTaskStateByTaskId(option.getTaskId(),
					finished ? IComputeTaskDAO.STATE_FINISHED
							: IComputeTaskDAO.STATE_ERROR);
			dao.close();
			dao = null;

		} catch (ClassNotFoundException e) {
			LOGGER.error("Required class org.ecnu.smartscore.runner."
					+ className + " not loaded!");
		} catch (Exception e) {
			LOGGER.error("Unknwon error in task runner " + className, e);
		}
	}
}
