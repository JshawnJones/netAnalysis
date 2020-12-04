package com.jshawn.netanalysis.network;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.jshawn.netanalysis.profile.userprofileRepository;
import com.jshawn.netanalysis.secureweb.usersRepository;

@Controller
public class NetworkController {

	@Autowired
	private userprofileRepository userprofileRepository;
	
	@Autowired
	private usersRepository usersRepository;
	
	@GetMapping(path="/network")
	public String handleNetwork(Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String username = "";
		if(auth.getPrincipal() instanceof java.lang.String) {
			
			username = (String) auth.getPrincipal();
			
		} else if (auth.getPrincipal() instanceof org.springframework.security.core.userdetails.UserDetails) {
			UserDetails userDetails = (UserDetails) auth.getPrincipal();
			
			username = userDetails.getUsername();
		}
		
		String profilePic = userprofileRepository.findByUsername(username).get(0).getProfilepic();
		String role = usersRepository.findByUsername(username).get(0).getRole();
		model.addAttribute("role", role);	

		model.addAttribute("username", username);
		model.addAttribute("profilePic", profilePic);
		return "network";
	}
	
	
}