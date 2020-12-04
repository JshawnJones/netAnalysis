package com.jshawn.netanalysis.files;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.CharacterIterator;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.StringCharacterIterator;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.jshawn.netanalysis.profile.userprofileRepository;
import com.jshawn.netanalysis.secureweb.usersRepository;

import org.apache.commons.io.FileUtils;
import org.apache.tomcat.util.codec.binary.Base64;

@Controller
public class FilesController {

	@Autowired
	private userprofileRepository userprofileRepository;

	@Autowired
	private usersRepository usersRepository;
	
	@GetMapping(path="/files")
	public String handleNetwork(Model model) throws IOException {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String username = "";
		if(auth.getPrincipal() instanceof java.lang.String) {
			
			username = (String) auth.getPrincipal();
			
		} else if (auth.getPrincipal() instanceof org.springframework.security.core.userdetails.UserDetails) {
			UserDetails userDetails = (UserDetails) auth.getPrincipal();
			
			username = userDetails.getUsername();
		}
		
		String profilePic = userprofileRepository.findByUsername(username).get(0).getProfilepic();
		
		String networkFiles = getFiles("E:\\");

		String role = usersRepository.findByUsername(username).get(0).getRole();
		model.addAttribute("role", role);	
		model.addAttribute("files", networkFiles);
		model.addAttribute("mainFolder", "E:\\");
		model.addAttribute("username", username);
		model.addAttribute("profilePic", profilePic);
		
		return "files";
	}
	
	@RequestMapping(path= { "/files/*" , "/files/**"})
	public String handlegetFolder(Model model, HttpServletRequest request) throws IOException {
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String username = "";
		if(auth.getPrincipal() instanceof java.lang.String) {
			
			username = (String) auth.getPrincipal();
			
		} else if (auth.getPrincipal() instanceof org.springframework.security.core.userdetails.UserDetails) {
			UserDetails userDetails = (UserDetails) auth.getPrincipal();
			
			username = userDetails.getUsername();
		}
		
		String profilePic = userprofileRepository.findByUsername(username).get(0).getProfilepic();
		
		String resulturlString = java.net.URLDecoder.decode(request.getRequestURI(), StandardCharsets.UTF_8.name());
		
		String folderName = resulturlString.substring(7);

		String networkFiles = getFiles("E:\\" + folderName);
		
		model.addAttribute("files", networkFiles);
		model.addAttribute("mainFolder", folderName);
		model.addAttribute("username", username);
		model.addAttribute("profilePic", profilePic);
		
		return "files";
	}
	
	@PostMapping(path="/getfiles/info")
	public @ResponseBody String handlegetFileInfo(Model model, @RequestParam("path") String filePath) throws IOException, URISyntaxException {
		String absolutePath = "";
		
		if(filePath.equals("E:\\")) {
			
			absolutePath = "E:";
			 
		} else {
			absolutePath = "E:\\" + java.net.URLDecoder.decode(filePath, StandardCharsets.UTF_8.name());
		}

		Path path = Paths.get(absolutePath);
		BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class);
		
		DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		
		String resulturlString = java.net.URLEncoder.encode(absolutePath, StandardCharsets.UTF_8.name());
		String resultFiles = "[{";
		
		if(filePath.equals("E:\\")) {
			resultFiles = resultFiles + "\"name\":\"E Drive\",";
			resultFiles = resultFiles + "\"absolutePath\":\"E:\",";
		} else {
			resultFiles = resultFiles + "\"name\":\""+path.getFileName().toString()+"\",";
			resultFiles = resultFiles + "\"absolutePath\":\""+resulturlString+"\",";
		}
		
		resultFiles = resultFiles + "\"creationTime\":\""+df.format(attr.creationTime().toMillis())+"\",";
		resultFiles = resultFiles + "\"lastAccessTime\":\""+df.format(attr.lastAccessTime().toMillis())+"\",";
		resultFiles = resultFiles + "\"lastModifiedTime\":\""+df.format(attr.lastModifiedTime().toMillis())+"\",";
		resultFiles = resultFiles + "\"isDirectory\":\""+attr.isDirectory()+"\",";
		resultFiles = resultFiles + "\"mimetype\":\""+Files.probeContentType(path)+"\",";
		resultFiles = resultFiles + "\"size\":\""+humanReadableByteCountSI(attr.size())+"\"";
		
		resultFiles = resultFiles + "}]";
		
		return resultFiles;
	}
	
	@PostMapping(path="/download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public @ResponseBody String handleDownloadFile(Model model, @RequestParam("path") String filePath) throws IOException {
		
		String absolutePath = "E:\\" + java.net.URLDecoder.decode(filePath, StandardCharsets.UTF_8.name());

		File file = new File(absolutePath);
		byte[] encoded = Base64.encodeBase64(FileUtils.readFileToByteArray(file));
		
	    return new String(encoded, StandardCharsets.US_ASCII);
	}
	
	@PostMapping(path="/deleteFile", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public @ResponseBody String handleDeleteFile(Model model, @RequestParam("path") String filePath,
			@RequestParam("type") String fileType) throws IOException {
		
		String absolutePath = "E:\\" + java.net.URLDecoder.decode(filePath, StandardCharsets.UTF_8.name());
		
		
		if(fileType.equals("folder")) {
			File folder = new File(absolutePath);
			
			FileUtils.deleteDirectory(folder);
		} else {
			
			File file = new File(absolutePath); 
			file.delete();
		}
		

		
	    return "files";
	}
	
	@PostMapping(path="/newFolder", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public @ResponseBody String handleNewFolder(Model model, @RequestParam("path") String filePath, 
			@RequestParam("name") String name) throws IOException {
		
		String absolutePath = "";
		
		if(filePath.equals("E:\\")) {
			absolutePath = "E:";
		} else {
			absolutePath = "E:\\" + java.net.URLDecoder.decode(filePath, StandardCharsets.UTF_8.name());
		}

		new File(absolutePath + "\\" + name).mkdirs();
	    return "";
	}
	
	@PostMapping(path="/fileupload")
	public @ResponseBody String handleFileUpload(@RequestParam("file") MultipartFile file, 
			@RequestParam("path") String path, @RequestParam("fileName") String fileName) 
					throws IllegalStateException, IOException {
		
		//place file in path
		byte[] bytes = file.getBytes();
		String insPath =  "";
		
		if(path.equals("E:\\")) {
			insPath =  path +"\\" +fileName;
		} else {
			insPath =  "E:\\" + path +"\\" +fileName;
		}
		
		Files.write(Paths.get(insPath), bytes);
		
		return "";
	}
	
	private String getFiles(String dir) throws IOException{
		//create new string
		String resultFiles = "[{";
		
		int counter = 0;
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(dir))) {
	        for (Path path : stream) {
	        	counter = counter +1;

	        	String file = "\""+path.getFileName()+"\": {" +
			            "\"name\":\""+path.getFileName()+"\"," +
			            "\"path\":\""+path.toString().substring(3).replace('\\', '/')+"\",";
	            if (!Files.isDirectory(path)) {
	            	file = file + "\"type\":\"file\"";
	            } else {
	            	file = file + "\"type\":\"folder\"";
	            }
	            file = file + "}";
	            
	            
	            if (counter == 1) {
	            	resultFiles = resultFiles + file;
	        	} else {
	        		resultFiles = resultFiles + "," + file;
	        	}
	            
	        }
	    }
		
		resultFiles = resultFiles + "}]";
		
		return resultFiles;
	}
	

	
	public static String humanReadableByteCountSI(long bytes) {
	    if (-1000 < bytes && bytes < 1000) {
	        return bytes + " B";
	    }
	    CharacterIterator ci = new StringCharacterIterator("kMGTPE");
	    while (bytes <= -999_950 || bytes >= 999_950) {
	        bytes /= 1000;
	        ci.next();
	    }
	    return String.format("%.1f %cB", bytes / 1000.0, ci.current());
	}
}