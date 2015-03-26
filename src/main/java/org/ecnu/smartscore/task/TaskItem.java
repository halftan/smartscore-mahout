package org.ecnu.smartscore.task;

import org.ecnu.smartscore.configs.ServerConfig;
import org.ecnu.smartscore.dao.DAOFactory;
import org.ecnu.smartscore.dao.IComputeTaskDAO;
import org.ecnu.smartscore.utils.ArrayUtils;
import org.ecnu.smartscore.utils.ZipCompress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.Map;

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
        boolean hasError = true;

        this.composeCommand();

        if (command != null && !command.isEmpty()) {
            IComputeTaskDAO dao = null;
            try {
                dao = DAOFactory.getComputeTaskDAOInstance();
                dao.updateComputeTaskStatusByTaskId(option.getTaskId(),
                        IComputeTaskDAO.STATE_RUNNING);
                hasError = this.invokeRunner();
                if (hasError) {
                    dao.updateComputeTaskStatusByTaskId(option.getTaskId(),
                            IComputeTaskDAO.STATE_ERROR);
                } else {
                    dao.updateComputeTaskStatusByTaskId(option.getTaskId(),
                            IComputeTaskDAO.STATE_FINISHED);
                    dao.updateFinishedTime(option.getTaskId());
                }
            } catch (SQLException e) {
                LOGGER.error("[task-{}] ; Update database error.", option.getTaskId());
            } catch (Exception e) {
                if (dao != null) {
                    dao.updateComputeTaskStatusByTaskId(option.getTaskId(),
                            IComputeTaskDAO.STATE_ERROR);
                }
                LOGGER.error("[task-{}] ; Unknown error.", option.getTaskId());
            } finally {
                if (dao != null) {
                    dao.close();
                    dao = null;
                }
            }
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
    }

    private Process getProcess() {
        ProcessBuilder pb = new ProcessBuilder("bash", "-c", command);
        LOGGER.debug("[task-{}] ; running command ; {}", option.getTaskId(), pb.command());
        Map<String, String> env = pb.environment();
        env.put("HADOOP_HOME", ServerConfig.getString("hadoop.home"));
        env.put("HADOOP_CONF_DIR", ServerConfig.getString("hadoop.conf.dir"));
        env.put("PATH", ServerConfig.getString("hadoop.bin") + File.pathSeparator + env.get("PATH"));
        File errLog = new File(String.format("%s/task-%d-error.log",
                ServerConfig.getString("sc.task.log_path"), option.getTaskId()));
        File stdLog = new File(String.format("%s/task-%d-output.log",
                ServerConfig.getString("sc.task.log_path"), option.getTaskId()));
        pb.redirectError(errLog);
        pb.redirectOutput(stdLog);
        try {
            Process p = pb.start();
            return p;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean invokeRunner() {
        try {
            Process p = getProcess();
            boolean hasErrorOutput = false;
            int returnVal = p.waitFor();
            hasErrorOutput = returnVal != 0;
            Jedis redis = null;
            if (option.getReturnKey() != null) {
                redis = new Jedis(ServerConfig.getString("sc.redis.host"),
                        ServerConfig.getInteger("sc.redis.port"));
            }
            if (redis != null) {
                redis.set(option.getReturnKey(), String.valueOf(returnVal));
            }
            if (hasErrorOutput) {
                LOGGER.error("[task-{}] ; Task error with return value {}.", option.getTaskId(), returnVal);
            } else {
                LOGGER.info("[task-{}] ; Task completed.", option.getTaskId());
            }
            return hasErrorOutput;
        } catch (InterruptedException e) {
            LOGGER.error("[task] ; [{}] ; Task interrupted.", option.getTaskId());
            return false;
        }

    }

}
