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

	/**
	 * Get ComputeTask instance by task ID.
	 * 
	 * @param taskId
	 * @return
	 */
	ComputeTask getComputeTaskByTaskId(int taskId);

	void updateComputeTaskStateByTaskId(int taskId, short state);

}
