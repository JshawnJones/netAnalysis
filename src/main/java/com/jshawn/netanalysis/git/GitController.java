package com.jshawn.netanalysis.git;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.jshawn.netanalysis.profile.userprofileRepository;
import com.jshawn.netanalysis.secureweb.usersRepository;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand.ListMode;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.errors.AmbiguousObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.errors.RevisionSyntaxException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.TreeWalk;

@Controller
public class GitController {

	@Autowired
	private userprofileRepository userprofileRepository;
	
	@Autowired
	private usersRepository usersRepository;
	
	public String gitRootFolder = "C:\\Users\\Jshawn\\git";
	
	@GetMapping(path="/git")
	public String handleGitRepos(Model model) throws IOException {
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
		
		String gitRepos = getGitRepos();
		
		
		model.addAttribute("gitRepos", gitRepos);	
		model.addAttribute("role", role);	
		model.addAttribute("username", username);
		model.addAttribute("profilePic", profilePic);

		return "git";
	}
	
	@RequestMapping(path= { "/git/commits/**" })
	public String handleGitCommits(Model model, HttpServletRequest request) throws IOException {
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
		String repoTitle = request.getRequestURI().replace("/git/code/", "").replace("/", "\\");
		
		String requesurlReplaced = repoTitle.replace("\\", "///");
		
		String rootpath = "";
		String base64 = "";
		String newBaseDir = "";
		String repoTitleToReturn = "";
		
		if(!requesurlReplaced.contains("///")) {
			
			rootpath = repoTitle;
			repoTitleToReturn = repoTitle;
		} else {
			base64 = requesurlReplaced.split("///")[0];

			rootpath = requesurlReplaced.replace(base64, "");
			
			rootpath = rootpath.substring(3, rootpath.length());
			repoTitle = repoTitle.replace(base64, "");
			newBaseDir = repoTitle;
						
			repoTitle = repoTitle.replace("\\", "///").split("///")[1];
			
			newBaseDir = newBaseDir.replace(repoTitle, "");
			
			
			base64 = requesurlReplaced.split("///")[3];
			if(newBaseDir.length() == 1) {
				repoTitleToReturn = repoTitle;
				
			} else {
				newBaseDir = newBaseDir.substring(2, newBaseDir.length());
				repoTitleToReturn = repoTitle + "/" + newBaseDir.replace("\\", "/");
			}
			
		}
		
		
		Git git = Git.open(new File(gitRootFolder + "\\" + rootpath.split("///")[3]));
		
		
		
		String branches = getBranchList(git);
		if(base64.equals("")) {
			try {
				List<Ref> branchlist = git.branchList().setListMode(ListMode.REMOTE).call();
				
				base64 = Base64.getEncoder().encodeToString(branchlist.get(0).getName().getBytes());
			} catch (GitAPIException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		String commitsJson = getCommits(git, base64);
		
		model.addAttribute("selectedBranch", base64);
		model.addAttribute("repoBranches", branches);
		model.addAttribute("role", role);	
		model.addAttribute("username", username);
		model.addAttribute("profilePic", profilePic);
		model.addAttribute("commitsJson", commitsJson);
		model.addAttribute("repoTitle", repoTitleToReturn);
		return "gitCommits";
	}
	
	@RequestMapping(path= { "/git/compareCommits/**" })
	public String handleGitCompareCommits(Model model, HttpServletRequest request) throws IOException {
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

		String repoTitleToReturn = request.getRequestURI().replace("/git/compareCommits/", "");
		String gitRepos = getGitReposByName(repoTitleToReturn);

		
		Git git = Git.open(new File(gitRootFolder + "\\" + repoTitleToReturn));
		String branches = getBranchList(git);
		model.addAttribute("repoBranches", branches);
		
		model.addAttribute("gitRepos", gitRepos);	
		model.addAttribute("role", role);	
		model.addAttribute("username", username);
		model.addAttribute("profilePic", profilePic);
		
		return "gitCompareCommits";
	}
	
	  @PostMapping(path="/git/getCompareCommits/**") 
	  public @ResponseBody String getCompareCommits (HttpServletRequest request,
			  @RequestParam String branch1,
			  @RequestParam String branch2) throws IOException, GitAPIException {

		  String repoTitleToReturn = request.getRequestURI().replace("/git/getCompareCommits/", "");
		  
		  Git git = Git.open(new File(gitRootFolder + "\\" + repoTitleToReturn));
		  Repository repository = git.getRepository();
		  
		  AbstractTreeIterator oldTreeParser = prepareTreeParser(repository, decodeBase64(branch1));
		  AbstractTreeIterator newTreeParser = prepareTreeParser(repository, decodeBase64(branch2));
		  
		  List<DiffEntry> diff = git.diff().setOldTree(oldTreeParser).setNewTree(newTreeParser).call();
		  
		  String result = "[{";
		  int counter = 1;
		  for(DiffEntry entry : diff) {
		      
				if (counter == 1) {
					result = result + "\"" + counter +"\": {" +
							"\"change\":\"" + entry.getChangeType() + "\""+ "," +
							"\"path\":\"" + entry.getOldPath() + "\""+	
							"}";
					
				} else {
					result = result + "," + "\"" + counter +"\": {" +
							"\"change\":\"" + entry.getChangeType() + "\""+ "," +
							"\"path\":\"" + entry.getOldPath() + "\""+	
							"}";
				}
		      
		      counter++;
		  }
		  
		  result = result + "}]";
		  
	    return result;
	  }
	
	@RequestMapping(path= { "/git/code/**" })
	public String handleGitFileCode(Model model, HttpServletRequest request) throws IOException {
		
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
		String repoTitle = request.getRequestURI().replace("/git/code/", "").replace("/", "\\");
		
		String requesurlReplaced = repoTitle.replace("\\", "///");
		
		String rootpath = "";
		String base64 = "";
		String newBaseDir = "";
		String repoTitleToReturn = "";
		
		if(!requesurlReplaced.contains("///")) {
			
			rootpath = repoTitle;
			repoTitleToReturn = repoTitle;
		} else {
			base64 = requesurlReplaced.split("///")[0];

			rootpath = requesurlReplaced.replace(base64, "");
			
			rootpath = rootpath.substring(3, rootpath.length());
			repoTitle = repoTitle.replace(base64, "");
			newBaseDir = repoTitle;
						
			repoTitle = repoTitle.replace("\\", "///").split("///")[1];
			
			newBaseDir = newBaseDir.replace(repoTitle, "");
			
			if(newBaseDir.length() == 1) {
				repoTitleToReturn = repoTitle;
				
			} else {
				newBaseDir = newBaseDir.substring(2, newBaseDir.length());
				repoTitleToReturn = repoTitle + "/" + newBaseDir.replace("\\", "/");
			}
			
			
			
		}
		File repofile  = new File(gitRootFolder + "\\" + repoTitle);
		
		Git git = Git.open(new File(gitRootFolder + "\\" + rootpath.split("///")[0]));
		String branches = getBranchList(git);
		if(base64 == "") {
			try {
				List<Ref> branchlist = git.branchList().setListMode(ListMode.REMOTE).call();
				
				base64 = Base64.getEncoder().encodeToString(branchlist.get(0).getName().getBytes());
			} catch (GitAPIException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		

		model.addAttribute("selectedBranch", base64);
		
		model.addAttribute("repoBranches", branches);
		model.addAttribute("role", role);	
		model.addAttribute("username", username);
		model.addAttribute("profilePic", profilePic);
		model.addAttribute("repoTitle", repoTitleToReturn);

		boolean isFile = false;
		String content = "";
		List<String> lines = new ArrayList<String>();

		Repository repo = git.getRepository();
		RevWalk revWalk = new RevWalk(repo);
		
//		byte[] decodedBytes = Base64.getDecoder().decode(base64);
//		String decodedString = new String(decodedBytes);
		String decodedString = decodeBase64(base64);
		
		RevCommit commit = revWalk.parseCommit(repo.findRef(decodedString).getObjectId());		
		ObjectId treeId = commit.getTree();
		String result = "[{";
		
		List<String> checkForDuplicatesList = new ArrayList<String>();
		HashMap<String, String> resultsList = new HashMap<String, String>();
		
		
		try (TreeWalk treeWalk = new TreeWalk(repo)) {
			  treeWalk.reset(treeId);
			  treeWalk.setRecursive(true);
			  while (treeWalk.next()) {
			    String path = treeWalk.getPathString();
				
				
			    if(path.equals(newBaseDir.replace("\\", "/"))) {
			    	
			    	isFile = true;
				    ObjectId objectId = treeWalk.getObjectId(0);
			        ObjectLoader loader = repo.open(objectId);
			        
			        
			        content = new String(loader.getBytes());
			        String[] contentLine = content.split("\\r?\\n");
			        
			        for(String line: contentLine) {
			        	
			        	lines.add(line);
			        }
			        
			    	break;
			    }
			    
			    if(!newBaseDir.equals("") && !newBaseDir.equals("\\")) {
			    	String splitcounter = "";
			    	if(newBaseDir.contains("\\")) {
			    		for(int k = 0; k < newBaseDir.replace("\\", "//").split("//").length; k++) {
				    		splitcounter = splitcounter +"/" + newBaseDir.replace("\\", "//").split("//")[k];
				    	}
			    		splitcounter = splitcounter.substring(1, splitcounter.length());
			    	} else {
			    		splitcounter = newBaseDir;
			    	}
			    	
			    	
			    	if(path.contains(splitcounter + "/")) {

			    		String testpath = path.replace(splitcounter+ "/", "");
			    		
					    if(checkForDuplicatesList.contains(testpath.split("/")[0])) {
					    	
					    } else {
					    	
					    	//check if is directory, 1 is dir
					    	if(testpath.split("/").length == 1 ) {
					    		resultsList.put(testpath.split("/")[0], "file");
					    	} else {
					    		resultsList.put(testpath.split("/")[0], "folder");
					    	}
					    	
					    	checkForDuplicatesList.add(testpath.split("/")[0]);
					    }
			    	}

			    } else {
				    if(checkForDuplicatesList.contains(path.split("/")[0])) {
				    	
				    } else {
				    	
				    	//check if is directory, 1 is dir
				    	if(path.split("/").length == 1 ) {
				    		resultsList.put(path.split("/")[0], "file");
				    	} else {
				    		resultsList.put(path.split("/")[0], "folder");
				    	}
				    	
				    	checkForDuplicatesList.add(path.split("/")[0]);
				    }
			    
			    }


			  }
		}
		

		
		if(isFile) {
			
			
			model.addAttribute("fileCode", lines);
			return "gitFileCode";
		} else {
			
			for(String pathItem: checkForDuplicatesList) {
				
				result = result + "\""+pathItem+"\":\"" + resultsList.get(pathItem) + "\""+ ",";

			}
			
			result = result.substring(0, result.length() - 1);
			result = result + "}]";
			
			model.addAttribute("repoFiles", result);
			
			
			return "gitrepo";
		}

		
	}
	
	
	private String getGitRepos() throws IOException {
		
		File f = new File(gitRootFolder);
		ArrayList<File> files = new ArrayList<File>(Arrays.asList(f.listFiles()));
		
		//create new string
		String result = "[{";
		int counter = 0;
		for (File item : files) { 
			counter = counter +1;
			boolean check = new File(item.getAbsolutePath(), ".git").exists();
			
			String absolutePath = item.getAbsolutePath().replace("\\", "\\\\");
			
			Git git = Git.open(new File(absolutePath));
			
			String headbranch = "";
			
			
			try {

				headbranch = Base64.getEncoder().encodeToString(git.branchList().setListMode(ListMode.REMOTE).call().get(0).getName().getBytes());
			} catch (GitAPIException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			Repository repository = git.getRepository();
			String url = repository.getConfig().getString("remote", "origin", "url");
			
			String latestCommit = getLatstCommit(git, repository);
			
			if(check) {
				if (counter == 1) {
					result = result + "\"" + item.getName() +"\": {" +
							"\"path\":\"" + absolutePath + "\""+ "," +
							"\"headbranch\":\"" + headbranch + "\""+ "," +
							"\"latestCommit\":\"" + latestCommit + "\""+ "," +
							"\"url\":\"" + url + "\""+	
							"}";
					
				} else {
					result = result + "," + "\"" + item.getName() +"\": {" +
							"\"path\":\"" + absolutePath + "\""+ "," +
							"\"headbranch\":\"" + headbranch + "\""+ "," +
							"\"latestCommit\":\"" + latestCommit + "\""+ "," +
							"\"url\":\"" + url + "\""+	
							"}";
				}
			}
		}
		
		result = result + "}]";
		
		return result;
	}
	
	
	private String getGitReposByName(String name) throws IOException {
		
		String result = "[";

		Git git = Git.open(new File(gitRootFolder + "\\" + name));
			
		String headbranch = "";
		try {
			headbranch = Base64.getEncoder().encodeToString(git.branchList().setListMode(ListMode.REMOTE).call().get(0).getName().getBytes());
		} catch (GitAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Repository repository = git.getRepository();
		String url = repository.getConfig().getString("remote", "origin", "url");
		
		String latestCommit = getLatstCommit(git, repository);
			

		result = result + "{" +
				"\"repoName\":\"" + name + "\""+ "," +
				"\"headbranch\":\"" + headbranch + "\""+ "," +
				"\"latestCommit\":\"" + latestCommit + "\""+ "," +
				"\"url\":\"" + url + "\"" +
				"}";

		
		result = result + "]";
		
		return result;
	}
	
	
	private String getLatstCommit(Git git, Repository repository) {
		
		String result = "";
		Date commitDate = null;
		try {
			
			List<Ref> branches = git.branchList().setListMode(ListMode.ALL).call();
			RevCommit youngestCommit = null;
			RevWalk revWalk = new RevWalk(repository);

			for(Ref branch : branches) {

				RevCommit commit = revWalk.parseCommit(branch.getObjectId());

				if(youngestCommit == null) {
					youngestCommit = commit;
				} else if(youngestCommit.getAuthorIdent().getWhen().compareTo(commit.getAuthorIdent().getWhen()) < 0){
					youngestCommit = commit;
				}
				
			}

			commitDate = youngestCommit.getAuthorIdent().getWhen();
		} catch (GitAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MissingObjectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IncorrectObjectTypeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd hh:mm");  
		result= formatter.format(commitDate);  

		
		return result;
	}
	
	String getGitFiles(String Dir, String repoTitle) {
		File directory = new File(Dir);
        
        File[] fList = directory.listFiles();
        
        List<File> gitFiles = new ArrayList<File>();
        List<File> gitFolder = new ArrayList<File>();
        List<File> gitOrder = new ArrayList<File>();
        for (File ffile : fList) {
        	if (ffile.isFile()) {
        		gitFiles.add(ffile);
        	} else if (ffile.isDirectory()) {
        		gitFolder.add(ffile);
        	}
        }
        gitOrder.addAll(gitFolder);
        gitOrder.addAll(gitFiles);
        
        
        String result = "[{";
        
        for (File file : gitOrder) {
        	String path = file.getAbsolutePath().replace(gitFolder + "\\", "").replace("\\", "\\\\");
        	String gitRootFolderAdjusted = gitRootFolder.replace("\\", "\\\\");
        	String repoTitleAdjusted = repoTitle.replace("\\", "\\\\");
        	path = path.replace(gitRootFolderAdjusted+"\\\\"+repoTitleAdjusted+"\\\\", "");
        	
            if (file.isFile() && !file.getName().equals(".git") && !file.getName().equals(".gitignore")) {
            	
            	result = result + "\"" + path +"\":\"file\",";
            } else if (file.isDirectory() && !file.getName().equals(".git") && !file.getName().equals(".gitignore")) {

            	result = result + "\"" + path +"\":\"folder\",";
            	
            }
        }
        result = result.substring(0, result.length() - 1);
        result = result + "}]";
		
		return result;
        
	}
	
	List<String> getAllFilesInDirectory(String Dir, String gitFolder){
		File directory = new File(Dir);
        List<String> resultList = new ArrayList<String>();
        File[] fList = directory.listFiles();

        for (File file : fList) {
            if (file.isFile()) {

            	String path = file.getAbsolutePath().replace(gitFolder + "\\", "");

            	resultList.add(path);
            } else if (file.isDirectory()) {
                resultList.addAll(getAllFilesInDirectory(file.getAbsolutePath(), gitFolder));
            }
        }
        
        return resultList;
	}
	
	String getBranchList(Git git){
		
		
		List<Ref> resultList = new ArrayList<Ref>();
		String result = "[{";
		int counter = 1;
		try {
			resultList = git.branchList().setListMode(ListMode.REMOTE).call();

			for(Ref ref: resultList) {

				String encodedString = Base64.getEncoder().encodeToString(ref.getName().getBytes());
				if (counter == 1) {
					result = result + "\"" + counter +"\": {" +
							"\"branch\":\"" + ref.getName() + "\""+ "," +
							"\"base64\":\"" + encodedString + "\""+	
							"}";
					counter++;
				} else {
					result = result + "," + "\"" + counter +"\": {" +
							"\"branch\":\"" + ref.getName() + "\""+ "," +
							"\"base64\":\"" + encodedString + "\""+	
							"}";
					counter++;
				}
				
			}

			
		} catch (GitAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		result = result + "}]";
		return result;
	}
	
	
	String getCommits(Git git, String selectedBranch) {
		Repository repository = git.getRepository();
		RevWalk revWalk = new RevWalk(repository);
		
//		byte[] decodedBytes = Base64.getDecoder().decode(selectedBranch);
//		String decodedString = new String(decodedBytes);
		String decodedString = decodeBase64(selectedBranch);
		
		String result = "[{";
		int counter = 1;
		try {
			Iterable<RevCommit> commits = git.log().add(repository.resolve(decodedString)).call();
		        for (RevCommit commit : commits) {
		        	RevCommit targetCommit = revWalk.parseCommit(repository.resolve(commit.getName()));
		        	
		        	String commitDate= new SimpleDateFormat("yyyy/MM/dd").format(new Date(targetCommit.getCommitTime() * 1000L));
		        			
		        	String[] commentmessage = targetCommit.getFullMessage().split("\\r?\\n");
		        	
		        	
		        	if (counter == 1) {
		        		result = result + "\"" + counter +"\": {" +
								"\"commitName\":\"" + targetCommit.getName() + "\""+ "," +
								"\"commitDate\":\"" + commitDate + "\"" + "," +
								"\"commitMessage\":\"" + commentmessage[0] + "\"" +	"," +
								"\"commitAuthor\":\"" + targetCommit.getAuthorIdent().getName() + "\"" + "," +
								"\"commitEmail\":\"" + targetCommit.getAuthorIdent().getEmailAddress() + "\"" +	
								"}";
		        		counter++;
		        	} else {
		        		result = result + "," + "\"" + counter +"\": {" +
								"\"commitName\":\"" + targetCommit.getName() + "\""+ "," +
								"\"commitDate\":\"" + commitDate + "\"" + "," +
								"\"commitMessage\":\"" + commentmessage[0] + "\"" +	"," +
								"\"commitAuthor\":\"" + targetCommit.getAuthorIdent().getName() + "\"" + "," +
								"\"commitEmail\":\"" + targetCommit.getAuthorIdent().getEmailAddress() + "\"" +	
								"}";
		        		counter++;
		        	}
		        	
		        	
		        }

		} catch (GitAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RevisionSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MissingObjectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IncorrectObjectTypeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AmbiguousObjectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		result = result + "}]";
		
		return result;
	}
	
	private String decodeBase64(String base64) {
		byte[] decodedBytes = Base64.getDecoder().decode(base64);
		String decodedString = new String(decodedBytes);
		
		return decodedString;
	}
	
	 private static AbstractTreeIterator prepareTreeParser(Repository repository, String ref) throws IOException {
	        // from the commit we can build the tree which allows us to construct the TreeParser
	        Ref head = repository.exactRef(ref);
	        try (RevWalk walk = new RevWalk(repository)) {
	            RevCommit commit = walk.parseCommit(head.getObjectId());
	            RevTree tree = walk.parseTree(commit.getTree().getId());

	            CanonicalTreeParser treeParser = new CanonicalTreeParser();
	            try (ObjectReader reader = repository.newObjectReader()) {
	                treeParser.reset(reader, tree.getId());
	            }

	            walk.dispose();

	            return treeParser;
	        }
	    }
}
