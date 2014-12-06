/**
 * 
 */
package org.ecnu.smartscore.dao.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.ecnu.smartscore.dao.IComputeTaskDAO;
import org.ecnu.smartscore.po.ComputeTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Microdog <dlangu0393@gmail.com>
 *
 */
public class ComputeTaskDAOImpl extends BaseDAOImpl implements IComputeTaskDAO {

	private final static Logger LOGGER = LoggerFactory
			.getLogger(ComputeTask.class);

	public ComputeTaskDAOImpl() throws SQLException {
		super();
	}

	@Override
	public ComputeTask getComputeTaskByTaskId(int taskId) {
		ComputeTask resultTask = null;

		String query = "SELECT `inputPath`, `outputPath`, `runner`, `username` FROM `tasks` JOIN `users` ON `tasks`.`uid` = `users`.`id` WHERE `tasks`.`id` = ? AND `state` = 0";
		try {
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setInt(1, taskId);
			LOGGER.debug(stmt.toString());
			stmt.execute();
			ResultSet rs = stmt.getResultSet();

			while (rs.next()) {
				resultTask = new ComputeTask(taskId, rs.getString(1),
						rs.getString(2), rs.getString(3), rs.getString(4));
			}

			rs.close();
			stmt.close();
		} catch (SQLException e) {
			LOGGER.error("An exception was caught in DAO.");
			LOGGER.error(e.toString());
			if (LOGGER.isDebugEnabled()) {
				e.printStackTrace();
			}
		}

		return resultTask;
	}

	@Override
	public void updateComputeTaskStateByTaskId(int taskId, short state) {
		String query = null;
		if (state == STATE_FINISHED) {
			query = "UPDATE `tasks` SET `state` = ?, `finishTime` = CURRENT_TIMESTAMP() WHERE `id` = ?";
		} else {
			query = "UPDATE `tasks` SET `state` = ? WHERE `id` = ?";
		}

		try {
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setShort(1, state);
			stmt.setInt(2, taskId);
			stmt.execute();
		} catch (SQLException e) {
			LOGGER.error("An exception was caught in DAO.");
			LOGGER.error(e.toString());
			if (LOGGER.isDebugEnabled()) {
				e.printStackTrace();
			}
		}
	}

}
