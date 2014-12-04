package org.ecnu.smartscore.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.JedisPubSub;

public class RedisListener extends JedisPubSub {

	private static Logger log = LoggerFactory.getLogger(RedisListener.class);

	@Override
	public void onMessage(String channel, String message) {
		System.out.println(String.format("Message from %s : %s", channel,
				message));
		if (message.equals("byebye")) {
			this.unsubscribe();
			TaskPool.getInstance().terminate();
			return;
		}

		TaskOption option = TaskOption.parse(message);
		if (option == null) {
			log.warn("Unacceptable message got: {}", message);
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
		System.out.println(String.format("Subscribe(%d) on %s",
				subscribedChannels, channel));
	}

	@Override
	public void onUnsubscribe(String channel, int subscribedChannels) {
		System.out.println(String.format("Unsubscribe(%d) on %s",
				subscribedChannels, channel));
	}

}
