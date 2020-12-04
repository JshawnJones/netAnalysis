package com.jshawn.netanalysis.profile;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.jshawn.netanalysis.secureweb.usersRepository;


@Controller
public class userprofileController {
	
	@Autowired
	private userprofileRepository userprofileRepository;
	
	@Autowired
	private usersRepository usersRepository;
	
	@GetMapping("/profile")
	public String profileInfo(Model model) {
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String username = "";
		if(auth.getPrincipal() instanceof java.lang.String) {
			
			username = (String) auth.getPrincipal();
			
		} else if (auth.getPrincipal() instanceof org.springframework.security.core.userdetails.UserDetails) {
			UserDetails userDetails = (UserDetails) auth.getPrincipal();
			
			username = userDetails.getUsername();
		}
		
		String profilePic = userprofileRepository.findByUsername(username).get(0).getProfilepic();
		
		model.addAttribute("username", username);
		model.addAttribute("profilePic", profilePic);
		
		return "profile";
	}


	@GetMapping("/")
	public String mainInfo(Model model) {
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//		UserDetails userDetails = (UserDetails) auth.getPrincipal();
		
//		String username = userDetails.getUsername();
		String username = "";
		if(auth.getPrincipal() instanceof java.lang.String) {
			
			username = (String) auth.getPrincipal();
			
		} else if (auth.getPrincipal() instanceof org.springframework.security.core.userdetails.UserDetails) {
			UserDetails userDetails = (UserDetails) auth.getPrincipal();
			
			username = userDetails.getUsername();
		}
		String profilePic = userprofileRepository.findByUsername(username).get(0).getProfilepic();
//		String role = userDetails.getAuthorities().toString();
		String role = usersRepository.findByUsername(username).get(0).getRole();
				
		model.addAttribute("username", username);
		model.addAttribute("profilePic", profilePic);
		model.addAttribute("role", role);
		
		return "main";
	}
}