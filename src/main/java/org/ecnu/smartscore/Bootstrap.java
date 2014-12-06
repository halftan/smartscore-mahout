package org.ecnu.smartscore;

import org.ecnu.smartscore.configs.ServerConfig;
import org.ecnu.smartscore.database.DatabaseConnection;
import org.ecnu.smartscore.redis.RedisListener;
import org.ecnu.smartscore.utils.LoggerLoader;

import redis.clients.jedis.Jedis;

public class Bootstrap {

	public static final void main(String[] args) {
		// Prepare logger
		LoggerLoader.loadLogger("smartscore.log");

		// Prepare config
		ServerConfig.load();

		// Prepare database connection pool
		DatabaseConnection.load();

		// Connect redis
		Jedis redis = new Jedis(ServerConfig.getString("sc.redis.host"),
				ServerConfig.getInteger("sc.redis.port"));
		redis.subscribe(new RedisListener(),
				ServerConfig.getString("sc.redis.channel"));
		redis.close();
	}
}
