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
	public final static short STATE_RUNNING = 1;
	public final static short STATE_FINISHED = 2;
	public final static short STATE_ERROR = 3;

	/**
	 * Get ComputeTask instance by task ID.
	 * 
	 * @param taskId
	 * @return
	 */
	ComputeTask getComputeTaskByTaskId(int taskId);

	void updateComputeTaskStateByTaskId(int taskId, short state);

}
