package com.jshawn.netanalysis.email;


import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import java.util.Properties;


@Component
public class EmailServiceImpl implements EmailService{
	
	@Autowired
	JavaMailSenderImpl mailSender;
	
    public String sendRegistrationEmail(String userEmail, String hash) {

    	//for mailhog
//    	mailSender.setHost("localhost");
//      mailSender.setPort(1025);
    	//for gmail
    	mailSender.setHost("smtp.gmail.com");
    	mailSender.setPort(587);
        mailSender.setProtocol("smtp");
        
        mailSender.setUsername("**************@gmail.com");
        mailSender.setPassword("***************");
    	
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");
        props.put("mail.smtp.ssl.trust", "*");
        
        
        //create new registration url
        String newRegiURL = "http://192.168.3.10:8080/newprofile/new/" +hash;
        
    	SimpleMailMessage message = new SimpleMailMessage(); 
        message.setFrom("admin@***********.com");
        message.setTo(userEmail); 
        message.setSubject("Jshawn network registration"); 
        message.setText("ネットワークにアクセスを許可します。 \n\n" +
        		"会員登録に進みに下記のリンクに進んでください: \n" + 
        		newRegiURL +"\n\n" +
        		"※本メールは送信専用のため、ご返信いただけません。");
        mailSender.send(message);
	     
        return newRegiURL;
	}
	
}