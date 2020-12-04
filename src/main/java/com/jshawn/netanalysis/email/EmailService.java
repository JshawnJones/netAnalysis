package com.jshawn.netanalysis.email;


public interface EmailService {

	String sendRegistrationEmail(String userEmail, String hash);
	
}