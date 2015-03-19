/**
 * 
 */
package org.ecnu.smartscore.task;

import org.codehaus.jackson.map.ObjectMapper;
import org.ecnu.smartscore.configs.ServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/**
 * @author Microdog <dlangu0393@gmail.com>
 *
 */
public class TaskOption {

    private static ArrayList<String> AVAILABLE_TASK_TYPE;
    private static HashMap<String, List<String>> TASK_TYPE_MODEL_MAP;

    static {
        AVAILABLE_TASK_TYPE = new ArrayList<>();
        TASK_TYPE_MODEL_MAP = new HashMap<>();
    }

    private final static Logger Log = LoggerFactory
            .getLogger(TaskOption.class);

    public static void main(String[] args) {
        ServerConfig.load();
        Process p;
        try {
            p = Runtime.getRuntime().exec("ls");
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(p.getInputStream())
            );
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
//        String message = "{\"taskType\":\"classification\", \"modelType\":\"ME\"}";
//        TaskOption option = TaskOption.parse(message);
//        Log.info("Option is {}", option);
    }

    public static List<String> getTaskModel(String taskType) {
        if (!TASK_TYPE_MODEL_MAP.containsKey(taskType)) {
            TASK_TYPE_MODEL_MAP.put(
                    taskType,
                    ServerConfig.getStringList("sc.task.types." + taskType + ".models")
            );
        }
        return TASK_TYPE_MODEL_MAP.get(taskType);
    }

    public static ArrayList<String> getAvailableTaskType() {
        if (AVAILABLE_TASK_TYPE.isEmpty()) {
            AVAILABLE_TASK_TYPE.addAll(ServerConfig.getStringList("sc.task.types"));
        }
        return AVAILABLE_TASK_TYPE;
    }

    public static boolean validateTaskType(TaskOption to) {
        return getAvailableTaskType().contains(to.getTaskType());
    }

    public static boolean validateModelType(TaskOption to) {
        return getTaskModel(to.getTaskType()).contains(to.getModelType());
    }

    public static TaskOption parse(String message) {
        ObjectMapper mapper = new ObjectMapper();
        TaskOption to = null;
        try {
            to = mapper.readValue(message, TaskOption.class);
            if (!validateTaskType(to) || !validateModelType(to)) {
                return null;
            }
            return to;
        } catch (IOException e) {
            Log.error("Parse message error.");
            Log.error("Message: {}", message);
            return null;
        }
	}

    public String getTaskType() {
        return taskType;
    }

    public String getModelType() {
        return modelType;
    }

    public String getInput() {
        return input;
    }

    public String getTrainInput() {
        return trainInput;
    }

    public String getOutput() {
        return output;
    }

    public String getParams() { return params; }

    public String getReturnKey() { return returnKey; }

    public int getTaskId() {
        return taskId;
    }

    public String taskType;
    public String modelType;
    public String input;
    public String trainInput;
    public String output;
    public String params;
    public String returnKey;
    public int taskId;

    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (IOException e) {
            return "";
        }
    }

}
