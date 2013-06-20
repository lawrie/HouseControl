package net.geekgrandad.plugin;

import com.sun.mail.smtp.SMTPTransport;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import net.geekgrandad.config.Config;
import net.geekgrandad.interfaces.EmailControl;
import net.geekgrandad.interfaces.Provider;
import net.geekgrandad.interfaces.Reporter;

public class EMail implements EmailControl {
	private String recipient;
	private Reporter reporter;

    private void send(String recipient, String cc, String subject, String message) throws AddressException, MessagingException {
        // Read the email properties file
    	Properties props = new Properties();
        try {
	        FileInputStream in = new FileInputStream("conf/email.properties");
	        props.load(in);
	        in.close();
        } catch (IOException e) {
        	System.out.println("Failed to load email properties");
        }
        
        String username = props.getProperty("username");
        String domain = props.getProperty("domain");
        String password = props.getProperty("password");
        String protocol = props.getProperty("protocol");
        String host = props.getProperty("mail.smtps.host");
        String charset = props.getProperty("charset");
        
        // Create a session and Mime message
        Session session = Session.getInstance(props, null);
        final MimeMessage msg = new MimeMessage(session);

        // Set the from, to cc, subject, message and sent date fields in the message
        msg.setFrom(new InternetAddress(username + "@" + domain));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient, false));

        if (cc.length() > 0) {
            msg.setRecipients(Message.RecipientType.CC, InternetAddress.parse(cc, false));
        }

        msg.setSubject(subject);
        msg.setText(message, charset);
        msg.setSentDate(new Date());
        
        // Create an SMTP transport and connect to the server
        SMTPTransport t = (SMTPTransport)session.getTransport(protocol);
        t.connect(host, username, password);
        
        // Send the message, and close the transport
        t.sendMessage(msg, msg.getAllRecipients());      
        t.close();
    }

	@Override
	public void setProvider(Provider provider) {
		Config config = provider.getConfig();
		reporter = provider.getReporter();
		recipient = config.emailRecipient;
	}

	@Override
	public void email(String subject, String msg) {
		try {
			send(recipient, "", subject, msg);
		} catch (AddressException e) {
			reporter.error("Address exception sending email");
		} catch (MessagingException e) {
			reporter.error("Messaging exception sending email");
		}
	}
}