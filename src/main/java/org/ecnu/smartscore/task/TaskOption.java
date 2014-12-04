/**
 * 
 */
package org.ecnu.smartscore.task;

/**
 * @author Microdog <dlangu0393@gmail.com>
 *
 */
public class TaskOption {

	private int taskId;
	private String runner;
	private String inputPath;

	public static TaskOption parse(String message) {
		String[] sections = message.split(" ");
		if (sections.length != 3) {
			return null;
		}
		try {
			int taskId = Integer.parseInt(sections[0]);
			return new TaskOption(taskId, sections[1], sections[2]);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @param taskId
	 * @param runner
	 * @param inputPath
	 */
	public TaskOption(int taskId, String runner, String inputPath) {
		super();
		this.taskId = taskId;
		this.runner = runner;
		this.inputPath = inputPath;
	}

	/**
	 * @return the taskId
	 */
	public int getTaskId() {
		return taskId;
	}

	/**
	 * @return the runner
	 */
	public String getRunner() {
		return runner;
	}

	/**
	 * @return the inputPath
	 */
	public String getInputPath() {
		return inputPath;
	}

	@Override
	public String toString() {
		return String.format("TaskOption(taskId=%d,runner=%s,inputPath=%s)",
				taskId, runner, inputPath);
	}

}
