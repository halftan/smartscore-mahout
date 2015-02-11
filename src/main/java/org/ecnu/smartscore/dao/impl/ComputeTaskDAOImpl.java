/**
 * 
 */
package org.ecnu.smartscore.dao.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.ecnu.smartscore.configs.ServerConfig;
import org.ecnu.smartscore.dao.IComputeTaskDAO;
import org.ecnu.smartscore.po.ComputeTask;
import org.ecnu.smartscore.task.TaskOption;
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

		String query = "SELECT * FROM `task` WHERE `task`.`id` = ? " +
                "AND `state` = " + String.valueOf(STATE_WAITING);
		try {
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setInt(1, taskId);
			LOGGER.debug("[SQL] {}", stmt.toString());
			stmt.execute();
			ResultSet rs = stmt.getResultSet();

			while (rs.next()) {
                String param = rs.getString(ServerConfig.getString("sc.database.task.content_column_name"));
				resultTask = new ComputeTask(TaskOption.parse(param));
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
        switch (state) {
            case STATE_FINISHED:
                query = "UPDATE `task` SET `state` = ?, `complete_at` = CURRENT_TIMESTAMP() WHERE `id` = ?";
                break;
            case STATE_RUNNING:
                query = "UPDATE `task` SET `state` = ?, `confirm_at` = CURRENT_TIMESTAMP() WHERE `id` = ?";
                break;
            default:
                query = "UPDATE `tasks` SET `state` = ? WHERE `id` = ?";
                break;
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
