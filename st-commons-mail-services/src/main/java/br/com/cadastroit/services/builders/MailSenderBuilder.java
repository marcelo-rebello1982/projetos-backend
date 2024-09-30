package br.com.cadastroit.services.builders;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class MailSenderBuilder {

    private String smtpHost;
    private Integer port;
    private boolean smtpAuth;
    private boolean sendPartial;
    private String timeout;
    private String connectionTimeout;
    private String protocol;
    private Logger LOGGER;
    private String username;
    private String password;

    public JavaMailSenderImpl createJavaMailSenderImpl(){
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(this.smtpHost);
        mailSender.setPort(this.port);
        mailSender.setUsername(this.username);
        mailSender.setPassword(this.password);

        Properties properties = mailSender.getJavaMailProperties();
        properties.put("mail.smtp.auth", this.smtpAuth);
        properties.put("mail.smtp.sendpartial", this.sendPartial);
        properties.put("mail.smtp.host", this.smtpHost);
        properties.put("mail.smtp.port", this.port);
//        properties.put("mail.debug", "true");

        if(this.smtpHost.equals("smtp.gmail.com")) {
            properties.put("mail.smtp.ssl.trust", "smtp.gmail.com");
            properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            properties.put("mail.smtp.socketFactory.fallback", false);
            properties.put("mail.smtp.starttls.enable", true);
            properties.put("mail.smtp.ssl.enable", true);
        }
        properties.put("mail.smtp.connectiontimeout", this.connectionTimeout);
        properties.put("mail.transport.protocol", this.protocol);
        return mailSender;
    }
}
