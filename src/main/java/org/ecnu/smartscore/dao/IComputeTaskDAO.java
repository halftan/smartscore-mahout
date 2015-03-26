/**
 * 
 */
package org.ecnu.smartscore.dao;

import org.ecnu.smartscore.po.ComputeTask;

/**
 * @author Microdog <dlangu0393@gmail.com>
 *
 */
public interface IComputeTaskDAO extends IBasicDAO {

	public final static short STATE_WAITING = 0;
    public final static short STATE_QUEUEING = 1;
	public final static short STATE_RUNNING = 2;
	public final static short STATE_FINISHED = 3;
	public final static short STATE_ERROR = -1;

	/**
	 * Get ComputeTask instance by task ID.
	 * 
	 * @param taskId
	 * @return
	 */
	ComputeTask getComputeTaskByTaskId(int taskId);

	void updateComputeTaskStatusByTaskId(int taskId, short state);

	void updateFinishedTime(int taskId);
}
