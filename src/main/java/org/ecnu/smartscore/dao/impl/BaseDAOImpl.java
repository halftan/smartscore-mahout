/**
 * 
 */
package org.ecnu.smartscore.dao.impl;

import java.sql.Connection;
import java.sql.SQLException;

import org.ecnu.smartscore.dao.IBasicDAO;
import org.ecnu.smartscore.database.DatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Microdog <dlangu0393@gmail.com>
 *
 */
public abstract class BaseDAOImpl implements IBasicDAO {

	private final static Logger LOGGER = LoggerFactory
			.getLogger(BaseDAOImpl.class);

	protected Connection conn;

	private volatile boolean connectionClosed = false;

	protected BaseDAOImpl() throws SQLException {
		conn = DatabaseConnection.getConnection();
	}

	@Override
	public void close() {
		try {
            if (!connectionClosed) {
                conn.close();
                connectionClosed = true;
            }
		} catch (SQLException e) {
			LOGGER.warn("Database error occurs when release a connection.");
			LOGGER.warn(e.toString());
			if (LOGGER.isDebugEnabled()) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void finalize() throws Throwable {
		// Release connection before destroyed
		if (!connectionClosed) {
			close();
		}
		super.finalize();
	}
}
