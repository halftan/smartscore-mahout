package org.ecnu.smartscore.task;

import redis.clients.jedis.Jedis;


public class TaskRunner implements Runnable {

	@Override
	public void run() {
		Jedis redis = new Jedis("localhost");
		redis.subscribe(new RedisListener(), "testchannel");
		redis.close();
	}

	
}
