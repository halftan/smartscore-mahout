package smartscore.task;

import redis.clients.jedis.Jedis;


public class MainTask {

	public static final void main(String[] args) {
		TaskPool taskPool = TaskPool.getInstance();
		taskPool.start();
		
		Jedis redis = new Jedis("localhost");
		redis.subscribe(new JedisListener(), "testchannel");
		redis.close();
	}
}
