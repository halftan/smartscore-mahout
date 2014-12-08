/**
 * 
 */
package org.ecnu.smartscore.mail;

import java.io.IOException;

import javax.activation.DataHandler;
import javax.mail.util.ByteArrayDataSource;

import org.ecnu.smartscore.task.TaskOption;

/**
 * @author Microdog <dlangu0393@gmail.com>
 *
 */
public class MailBuilder {

	private static final String TEMPLATE = "Task #%d done!";

	public static DataHandler buildHtmlMail(String html) throws IOException {
		return new DataHandler(new ByteArrayDataSource(html, "text/html"));
	}

	public static DataHandler buildHtmlMail(TaskOption option)
			throws IOException {
		return buildHtmlMail(String.format(TEMPLATE, option.getTaskId()));
	}

}
