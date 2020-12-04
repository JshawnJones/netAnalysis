package com.jshawn.netanalysis.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.jshawn.netanalysis.profile.TokenService;


@Controller
@RequestMapping(path="/profile")
public class EmailController {
	
	@Autowired
	private EmailServiceImpl mailSender;
	
	@Autowired 
	TokenService tokenService;
	
//	@Autowired
//	private userprofileRepository userprofileRepository;
//	
//	@Autowired
//	private usersRepository usersRepository;
	
	@PostMapping(path="/addByEmail")
	public String handleAddByEmail(@RequestParam("email") String email) {


        String hash = tokenService.getHash();
		
		String hashURL = mailSender.sendRegistrationEmail(email, hash);
		
		
		return "main";
	}
	
}