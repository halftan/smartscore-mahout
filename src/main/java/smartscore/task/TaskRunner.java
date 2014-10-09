package smartscore.task;

import redis.clients.jedis.Jedis;


public class TaskRunner implements Runnable {

	@Override
	public void run() {
		Jedis redis = new Jedis("localhost");
		redis.subscribe(new JedisListener(), "testchannel");
		redis.close();
	}

	
}
