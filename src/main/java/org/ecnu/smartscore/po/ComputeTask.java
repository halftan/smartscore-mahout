/**
 * 
 */
package org.ecnu.smartscore.po;

/**
 * @author Microdog <dlangu0393@gmail.com>
 *
 */
public class ComputeTask {

	private int taskId;
	private String inputPath;
	private String outputPath;
	private String runner;
	private String email;

	/**
	 * @param taskId
	 * @param inputPath
	 * @param outputPath
	 * @param runner
	 */
	public ComputeTask(int taskId, String inputPath, String outputPath,
			String runner, String email) {
		super();
		this.taskId = taskId;
		this.inputPath = inputPath;
		this.outputPath = outputPath;
		this.runner = runner;
		this.email = email;
	}

	/**
	 * @return the taskId
	 */
	public int getTaskId() {
		return taskId;
	}

	/**
	 * @return the inputPath
	 */
	public String getInputPath() {
		return inputPath;
	}

	/**
	 * @return the outputPath
	 */
	public String getOutputPath() {
		return outputPath;
	}

	/**
	 * @return the runner
	 */
	public String getRunner() {
		return runner;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

}
