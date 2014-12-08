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

	public static TaskOption parse(String message) {
		String[] sections = message.split("\\n");
		if (sections.length != 1) {
			return null;
		}
		try {
			int taskId = Integer.parseInt(sections[0]);
			return new TaskOption(taskId);
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
	public TaskOption(int taskId) {
		super();
		this.taskId = taskId;
	}

	/**
	 * @return the taskId
	 */
	public int getTaskId() {
		return taskId;
	}

}
