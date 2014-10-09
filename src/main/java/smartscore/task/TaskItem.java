package smartscore.task;

public class TaskItem implements Runnable {

	private String mOption;
	
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
			break;
		default:
			System.out.println("Not recognized.");
			break;
		}
	}
}
