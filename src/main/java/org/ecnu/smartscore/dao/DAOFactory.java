/**
 * 
 */
package org.ecnu.smartscore.dao;

import java.sql.SQLException;

import org.ecnu.smartscore.dao.impl.ComputeTaskDAOImpl;

/**
 * @author Microdog <dlangu0393@gmail.com>
 *
 */
public class DAOFactory {
	public static IComputeTaskDAO getComputeTaskDAOInstance()
			throws SQLException {
		return new ComputeTaskDAOImpl();
	}
}
