package org.ecnu.smartscore.task;

import org.ecnu.smartscore.configs.ServerConfig;
import org.ecnu.smartscore.dao.DAOFactory;
import org.ecnu.smartscore.dao.IComputeTaskDAO;
import org.ecnu.smartscore.utils.FilePath;
import org.ecnu.smartscore.utils.ZipCompress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.sql.SQLException;

public class TaskItem implements Runnable {

	private TaskOption option;
	private final static Logger LOGGER = LoggerFactory
			.getLogger(TaskItem.class);
    private String command;

	public TaskItem(TaskOption option) {
		this.option = option;

		LOGGER.info("Task created with options: {}", option);
	}

	@Override
	public void run() {

        this.composeCommand();

        if (command != null && !command.isEmpty()) {
            IComputeTaskDAO dao = null;
            try {
                dao = DAOFactory.getComputeTaskDAOInstance();
                dao.updateComputeTaskStateByTaskId(option.getTaskId(),
                        IComputeTaskDAO.STATE_RUNNING);
                dao.close();
                dao = null;
                this.invokeRunner();
            } catch (SQLException e) {
                LOGGER.error("[task] ; Update database error ; taskid ; {} ; not run", option.getTaskId());
            }
        }

        IComputeTaskDAO dao = null;
        try {
            dao = DAOFactory.getComputeTaskDAOInstance();
            dao.updateComputeTaskStateByTaskId(option.getTaskId(),
                    IComputeTaskDAO.STATE_FINISHED);
            dao.close();
            dao = null;
        } catch (SQLException e) {
            LOGGER.error("[task] ; Update database error ; taskid ; {} ; finished", option.getTaskId());
        }


//		String inputPath = FilePath.getFullInputPath(option.getInput());
//		String outputPath = FilePath.getFullOutputPath(option.getOutput());
//
//		switch (option.getModelType()) {
//		case "kmeans":
//			LOGGER.info("Running kmeans example case.");
//			invokeRunner(option.getTaskType(), "KmeansRunner", inputPath, outputPath);
//			LOGGER.info("Task K-Means finished successfully.");
//			break;
//		case "fuzzyk":
//			LOGGER.info("Runninng fuzzy kmeans example case.");
//			invokeRunner(option.getTaskType(), "FuzzyKmeansRunner", inputPath, outputPath);
//			LOGGER.info("Task Fuzzy K-Means finished successfully.");
//			break;
//		default:
//			LOGGER.error("Not recognized.");
//			return;
//		}

		// Send mail
//		try {
//			MailService.getInstance().sendMail(task.getEmail(),
//					MailBuilder.buildHtmlMail(option));
//		} catch (IOException e) {
//			LOGGER.error("Cannot send mail", e);
//		}
	}

    private void composeCommand() {
        String commandTmpl = ServerConfig.getString(
                String.format("sc.task.%s.%s", option.getTaskType(), option.getModelType())
        );

        if (commandTmpl == null || commandTmpl.isEmpty()) {
           this.command = null;
        }
        commandTmpl = commandTmpl.replaceAll("\\{inputPath\\}", option.getInput());
        commandTmpl = commandTmpl.replaceAll("\\{outputPath\\}", option.getOutput());

        this.command = commandTmpl;
        LOGGER.debug("[task] ; running command ; {} ; {}", option.getTaskId(), commandTmpl);
    }

    private void invokeRunner() {
        try {
            Process p = Runtime.getRuntime().exec(this.command.split(" "));
            p.waitFor();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(p.getErrorStream())
            );
            String line = null;
            boolean hasErrorOutput = false;
            while ((line = reader.readLine()) != null) {
                hasErrorOutput = true;
                LOGGER.error("[task] ; [{}] ; {}", option.getTaskId(), line);
            }
            if (hasErrorOutput) {
                LOGGER.error("[task] ; [{}] ; Task went south.", option.getTaskId());
            } else {
                LOGGER.info("[task] ; [{}] ; Task completed.", option.getTaskId());
            }
        } catch (IOException e) {
            LOGGER.error("[task] ; Cannot execute command ; {}", this.command);
        } catch (InterruptedException e) {
            LOGGER.error("[task] ; [{}] ; Task interrupted.", option.getTaskId());
        }

    }

	private void invokeRunner(String taskName, String className, String inputPath,
			String outputPath) {
		try {
			ClassLoader loader = this.getClass().getClassLoader();
			Class<?> runner = loader
					.loadClass("org.ecnu.smartscore." + taskName + ".runner." + className);
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
