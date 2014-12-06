/**
 * 
 */
package org.ecnu.smartscore.mail;

import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.ecnu.smartscore.configs.ServerConfig;

/**
 * @author Microdog <dlangu0393@gmail.com>
 *
 */
public class MailService {

	private static MailService selfInstance = null;

	Session session = null;
	Transport transport = null;
	String protocol = null;
	String mailHost = null;
	int port;
	String username = null;
	String password = null;

	public static MailService getInstance() {
		if (selfInstance == null) {
			MailService selfInstance = new MailService(
					ServerConfig.getString("sc.mail.protocol"),
					ServerConfig.getString("sc.mail.host"),
					ServerConfig.getInteger("sc.mail.port"),
					ServerConfig.getString("sc.mail.username"),
					ServerConfig.getString("sc.mail.password"));

			selfInstance.buildSession();
			selfInstance.connect();
			MailService.selfInstance = selfInstance;
		}
		return selfInstance;
	}

	public static void load() {
		MailService.getInstance();
	}

	private MailService(String protocol, String mailHost, int port,
			String username, String password) {
		this.protocol = protocol;
		this.mailHost = mailHost;
		this.port = port;
		this.username = username;
		this.password = password;
	}

	@Override
	protected void finalize() throws Throwable {
		disconnect();
		super.finalize();
	}

	private void _buildSession() throws NoSuchProviderException {
		Properties properties = new Properties();
		// SMTPS
		properties.put("mail.transport.protocol", "smtps");
		properties.put("mail.smtps.host", mailHost);
		properties.put("mail.smtps.auth", "true");
		properties.put("mail.smtps.quitwait", "false");

		Session mailSession = Session.getDefaultInstance(properties);
		mailSession.setDebug(true);

		Transport mailTransport = mailSession.getTransport();

		session = mailSession;
		transport = mailTransport;
	}

	public boolean buildSession() {
		try {
			_buildSession();
		} catch (NoSuchProviderException e) {
			return false;
		}
		return true;
	}

	private void _connect() throws MessagingException {
		if (transport != null) {
			transport.connect(mailHost, port, username, password);
		}
	}

	public boolean connect() {
		try {
			_connect();
		} catch (MessagingException e) {
			return false;
		}
		return true;
	}

	private void _disconnect() throws MessagingException {
		if (transport != null) {
			transport.close();
		}
	}

	public boolean disconnect() {
		try {
			_disconnect();
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	private void _sendMail(String to, String subject, String from, String cc,
			String bcc, DataHandler content) throws AddressException,
			MessagingException {
		// construct the message
		Message msg = new MimeMessage(session);
		if (from != null)
			msg.setFrom(new InternetAddress(from));
		else
			msg.setFrom();

		msg.setRecipients(Message.RecipientType.TO,
				InternetAddress.parse(to, false));
		if (cc != null)
			msg.setRecipients(Message.RecipientType.CC,
					InternetAddress.parse(cc, false));
		if (bcc != null)
			msg.setRecipients(Message.RecipientType.BCC,
					InternetAddress.parse(bcc, false));

		msg.setSubject(subject);

		msg.setDataHandler(content);

		msg.setHeader("X-Mailer", "Smartscore Mailer");
		msg.setSentDate(new Date());

		// send the thing off
		transport.sendMessage(msg, msg.getAllRecipients());
	}

	public boolean sendMail(String to, String subject, String from, String cc,
			String bcc, DataHandler content) {
		try {
			_sendMail(to, subject, from, cc, bcc, content);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public boolean sendMail(String to, DataHandler content) {
		try {
			_sendMail(to, ServerConfig.getString("sc.mail.subject"),
					ServerConfig.getString("sc.mail.from"),
					ServerConfig.getString("sc.mail.cc"),
					ServerConfig.getString("sc.mail.bcc"), content);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		MailService mailSender = new MailService("smtps", "smtp.gmail.com",
				465, "**Your email here**", "**Your password here**");

		mailSender.buildSession();
		mailSender.connect();
		mailSender.sendMail("**Send to here**", "Hello", null, null, null,
				MailBuilder.buildHtmlMail("Hi!"));
		mailSender.disconnect();
	}

}
