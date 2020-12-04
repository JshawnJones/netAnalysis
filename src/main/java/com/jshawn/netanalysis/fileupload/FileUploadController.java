package com.jshawn.netanalysis.fileupload;


import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.jshawn.netanalysis.profile.userprofile;
import com.jshawn.netanalysis.profile.userprofileRepository;
import com.jshawn.netanalysis.secureweb.users;
import com.jshawn.netanalysis.secureweb.usersRepository;

@Controller
@RequestMapping(path="/profile")
public class FileUploadController {

	@Autowired
	private userprofileRepository userprofileRepository;
	
	@Autowired
	private usersRepository usersRepository;
	
	@PostMapping(path="/fileupload")
	public String handleFileUpload(@RequestParam("file") MultipartFile file, 
			RedirectAttributes redirectAttributes) throws IllegalStateException, IOException {
		
		String filePath = "./src/main/resources/static/images/";
		
		// rename the file to date
		String Originalfilename = file.getOriginalFilename();
		String fileExtension = Originalfilename.substring(Originalfilename.lastIndexOf(".") + 1);
		
		//rename to current timestamp
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");  
		LocalDateTime now = LocalDateTime.now();  
		String timeStamp = dtf.format(now);
		
		String newFileName = timeStamp + "." + fileExtension;
		
		//place file in path
		byte[] bytes = file.getBytes();
		String insPath = filePath + newFileName;
		Files.write(Paths.get(insPath), bytes);

		//update sql database with name of new file
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String username = "";
		if(auth.getPrincipal() instanceof java.lang.String) {
			
			username = (String) auth.getPrincipal();
			
		} else if (auth.getPrincipal() instanceof org.springframework.security.core.userdetails.UserDetails) {
			UserDetails userDetails = (UserDetails) auth.getPrincipal();
			
			username = userDetails.getUsername();
		}
		
		List<userprofile>  profileList = userprofileRepository.findByUsername(username);
		
		userprofile newProfile = profileList.get(0);
		newProfile.setProfilepic("/images/" + newFileName);
		
		userprofileRepository.save(newProfile);
		
		return "profile";
	}
	
	@PostMapping(path="/profileName")
	public String handleNameUpdate(@RequestParam("name") String profileName)  {
		
		//update sql database with name of new file
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String username = "";
		if(auth.getPrincipal() instanceof java.lang.String) {
			
			username = (String) auth.getPrincipal();
			
		} else if (auth.getPrincipal() instanceof org.springframework.security.core.userdetails.UserDetails) {
			UserDetails userDetails = (UserDetails) auth.getPrincipal();
			
			username = userDetails.getUsername();
		}
		
		//userprofile name
		List<userprofile>  profileList = userprofileRepository.findByUsername(username);
		userprofile newProfile = profileList.get(0);
		newProfile.setUsername(profileName);
		userprofileRepository.save(newProfile);
		
		//users name
		List<users> userslist = usersRepository.findByUsername(username);
		users newUsers = userslist.get(0);
		newUsers.setUsername(profileName);
		usersRepository.save(newUsers);
		
		List<GrantedAuthority> updatedAuthorities = new ArrayList<>(auth.getAuthorities());
		Authentication newAuth = new UsernamePasswordAuthenticationToken(profileName, auth.getCredentials(), updatedAuthorities);

		SecurityContextHolder.getContext().setAuthentication(newAuth);
		
		return "profile";
	}
}
