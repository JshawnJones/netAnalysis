package com.jshawn.netanalysis.profile;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import com.jshawn.netanalysis.secureweb.users;
import com.jshawn.netanalysis.secureweb.usersRepository;

@Controller
@RequestMapping(path="/newprofile")
public class NewProfileController {

	@Autowired
	TokenService tokenService;
	
	@Autowired
	private userprofileRepository userprofileRepository;
	
	@Autowired
	private usersRepository usersRepository;
	
	@Autowired
	InMemoryUserDetailsManager inMemoryUserDetailsManager;
	
	@RequestMapping(path="/new/{hash}")
	public String handleNewProfile(@PathVariable String hash, Model model, 
			HttpServletRequest request) throws ServletException, ParseException  {
		
		if(tokenService.checkHash(hash)) {
			
			if(tokenService.checkExpiration(hash)) {
				model.addAttribute("defultPic", "/images/defult.jpg");
				
				if(request.getSession(false) == null) {

					try {
						request.login("temp", "temp");
					} catch (ServletException e) {
						return "newprofile";
								
					}
				} else {
					request.logout();
					request.login("temp", "temp");
				}
				
				

				request.getSession(true);
				
				return "newprofile";
			} else {
				return "error";
			}
			

			
			
		} else {
			
			return "error";
		}
		
	}
	
	
	@PostMapping(path="/new/save")
	public String handleNameUpdate(@RequestParam("file") MultipartFile file,
			@RequestParam("usernameP") String profileName,
			@RequestParam("userPass") String password,
			HttpServletRequest request
			) throws Exception  {
		
		String filePath = "C:\\Users\\Jshawn\\git\\netAnalysis\\src\\main\\resources\\static\\images\\";
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
		
		//add new user to mysql database
		//add to user
		users newUser = new users();
		newUser.setPass(password);
		newUser.setUsername(profileName);
		newUser.setRole("USER");
		
		usersRepository.save(newUser);
		
		//add to userprofile db
		userprofile newProfile = new userprofile();
		newProfile.setProfilepic("/images/" + newFileName);
		newProfile.setUsername(profileName);
		
		userprofileRepository.save(newProfile);

		//authorize new user
		List<UserDetails> userDetailsList = new ArrayList<>();
		userDetailsList.add(
				User.withUsername(profileName)
					.password(
							new BCryptPasswordEncoder()
						.encode(password)
					)
				.roles("USER").build());
		
		
		
		inMemoryUserDetailsManager.createUser(userDetailsList.get(0));
				
		request.logout();

		
		return "login";
	}
	
	
	@RequestMapping(path="/new/checkname")
	public @ResponseBody String CheckUsername(@RequestParam("usernameP") String profileName) {
		
		int size = usersRepository.findByUsername(profileName).size();
		
		String result = "nameTaken";
		if(size == 0) {
			result = "nameOK";
		}
		return result;
	}
	
}