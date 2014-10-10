package org.ecnu.smartscore.task;

import redis.clients.jedis.JedisPubSub;

public class RedisListener extends JedisPubSub {

	@Override
	public void onMessage(String channel, String message) {
		System.out.println(String.format("Message from %s : %s", channel, message));
		if (message.equals("byebye")) {
			this.unsubscribe();
			TaskPool.getInstance().terminate();
			return;
		}
		
		TaskItem th = new TaskItem(message);
		TaskPool.getInstance().put(th);
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
		System.out.println(String.format("Subscribe(%d) on %s", subscribedChannels, channel));
	}

	@Override
	public void onUnsubscribe(String channel, int subscribedChannels) {
		System.out.println(String.format("Unsubscribe(%d) on %s", subscribedChannels, channel));
	}

}
