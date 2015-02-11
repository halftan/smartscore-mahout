package org.ecnu.smartscore.redis;

import org.ecnu.smartscore.task.TaskItem;
import org.ecnu.smartscore.task.TaskOption;
import org.ecnu.smartscore.task.TaskPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.JedisPubSub;

public class RedisListener extends JedisPubSub {

	private final static Logger LOGGER = LoggerFactory
			.getLogger(RedisListener.class);

	@Override
	public void onMessage(String channel, String message) {
		LOGGER.debug("[task] ; Message from {} ; {}", channel, message);
		if (message.equals("byebye")) {
			this.unsubscribe();
			TaskPool.getInstance().terminate();
			return;
		}

		TaskOption option = TaskOption.parse(message);
		if (option == null) {
			LOGGER.warn("[task] ; Unacceptable message ; {}", message);
		} else {
			TaskPool.getInstance().put(new TaskItem(option));
		}
	}

	@Override
	public void onPMessage(String pattern, String channel, String message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPSubscribe(String arg0, int subscribedChannels) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPUnsubscribe(String arg0, int subscribedChannels) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSubscribe(String channel, int subscribedChannels) {
		LOGGER.info("Subscribe({}) on {}", subscribedChannels, channel);
	}

	@Override
	public void onUnsubscribe(String channel, int subscribedChannels) {
		LOGGER.info("Unsubscribe({}) on {}", subscribedChannels, channel);
	}

}
