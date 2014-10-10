package org.ecnu.smartscore.task;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskItem implements Runnable {

	private String mOption;
	private static Logger log;
	static {
		log = LoggerFactory.getLogger("TaskItem");
	}
	
	public TaskItem(String option) {
		mOption = option;
	}
	
	public Thread dispatch() {
		Thread th = new Thread(this, "Task " + mOption);
		return th;
	}

	@Override
	public void run() {
		switch (mOption) {
		case "bye":
			System.out.println("Quit.");
			break;
		case "kmeans":
			System.out.println("Running kmeans example case.");
			try {
				ClassLoader loader = this.getClass().getClassLoader();
				Class runner = loader.loadClass("org.ecnu.smartscore.runner.KmeansRunner");
				Method runMethod = runner.getMethod("run");
				runMethod.invoke(null);
			} catch (ClassNotFoundException e) {
				log.error("Required class org.ecnu.smartscore.runner.KmeansRunner not loaded!");
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		default:
			System.out.println("Not recognized.");
			break;
		}
	}
}
