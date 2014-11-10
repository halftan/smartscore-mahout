package org.ecnu.smartscore.task;

import redis.clients.jedis.Jedis;


public class Main {

	public static final void main(String[] args) {
		TaskPool taskPool = TaskPool.getInstance();
		taskPool.start();
		
		Jedis redis = new Jedis("localhost", 6379);
		redis.subscribe(new RedisListener(), "smartscore-taskqueue");
		redis.close();
	}
}
