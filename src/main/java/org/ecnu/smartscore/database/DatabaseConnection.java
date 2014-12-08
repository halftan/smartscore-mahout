/**
 * 
 */
package org.ecnu.smartscore.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import org.ecnu.smartscore.configs.ServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;

/**
 * @author Microdog <dlangu0393@gmail.com>
 *
 */
public final class DatabaseConnection {

	private final static Logger LOGGER = LoggerFactory
			.getLogger(DatabaseConnection.class);

	private volatile boolean isClosed = true;

	private DatabaseConnection() {
	}

	private Connection _getConnection() throws SQLException {
		Connection connection = null;
		try {
			connection = connectionPool.getConnection();
		} catch (SQLException e) {
			LOGGER.warn("Cannot get a free connection from database connection pool.");
			LOGGER.warn(e.toString());
			if (LOGGER.isDebugEnabled()) {
				e.printStackTrace();
			}
			// Throw the exception to external code
			throw e;
		}
		return connection;
	}

	/**
	 * Get a free connection from connection pool.
	 * 
	 * @return Database connection
	 * @throws SQLException
	 *             when a free connection cannot be got
	 */
	public static Connection getConnection() throws SQLException {
		return DatabaseConnectionInstance._getConnection();
	}

	/**
	 * Load database pool configurations.
	 * 
	 * @return BoneCPConfig
	 */
	private BoneCPConfig config() {
		BoneCPConfig config = new BoneCPConfig();
		config.setJdbcUrl(DBURL);
		config.setUsername(DBUSER);
		config.setPassword(DBPASS);
		config.setAcquireIncrement(ServerConfig
				.getInteger("sc.database.pool.acquireincrement"));
		config.setDisableConnectionTracking(!ServerConfig
				.getBoolean("sc.database.pool.connectiontracking"));
		config.setStatementsCacheSize(ServerConfig
				.getInteger("sc.database.pool.statementscachesize"));
		config.setMinConnectionsPerPartition(ServerConfig
				.getInteger("sc.database.pool.minconnectionsperpartition"));
		config.setMaxConnectionsPerPartition(ServerConfig
				.getInteger("sc.database.pool.maxconnectionsperpartition"));
		config.setPartitionCount(ServerConfig
				.getInteger("sc.database.pool.partitioncount"));
		config.setConnectionTimeout(2, TimeUnit.SECONDS);
		return config;
	}

	/**
	 * This method create a database connection pool and was called when this
	 * class was loaded.
	 * 
	 * @return Database connection pool
	 */
	private BoneCP create() {
		LOGGER.info("Creating database connection pool...");

		BoneCP pool = null;

		for (int i = 0; i != 3; ++i) { // Try to create database connection pool
			try {
				pool = new BoneCP(config());
				break; // If no exception was threw, break this loop
			} catch (SQLException e) {
				LOGGER.error("Cannot create database connection pool, retrying...");
				LOGGER.error(e.toString());
				if (LOGGER.isDebugEnabled()) {
					e.printStackTrace();
				}
			}
		}

		// Check if connection pool was created
		if (pool == null) {
			LOGGER.error("Cannot create database connection pool! Program terminated.");
			System.exit(1);
		}

		LOGGER.info("Database connection pool created.");
		isClosed = false;
		return pool;
	}

	@Override
	protected void finalize() throws Throwable {
		_close();
	}

	public synchronized void _close() {
		if (!isClosed) {
			isClosed = true;
			LOGGER.info("Closing database connection pool...");
			try {
				connectionPool.shutdown();
			} catch (Exception e) {
				LOGGER.error("Errors occured when db pool is closing...");
				LOGGER.error(e.toString());
				if (LOGGER.isDebugEnabled()) {
					e.printStackTrace();
				}
				isClosed = false;
			}
		}
	}

	public static void close() {
		DatabaseConnectionInstance._close();
	}

	public static synchronized void load() {
		if (DatabaseConnectionInstance == null) {
			DatabaseConnectionInstance = new DatabaseConnection();
		}
	}

	// Database driver
	public static String DBDRIVER = ServerConfig
			.getString("sc.database.driver");

	// Database name
	public final static String DBNAME = ServerConfig
			.getString("sc.database.name");

	// Database url
	public final static String DBURL = ServerConfig
			.getString("sc.database.url");

	// Database user
	public final static String DBUSER = ServerConfig
			.getString("sc.database.user");

	// Database password
	public final static String DBPASS = ServerConfig
			.getString("sc.database.password");

	// BoneCP database connection pool
	private BoneCP connectionPool = create();

	//
	private static DatabaseConnection DatabaseConnectionInstance = null;
}
