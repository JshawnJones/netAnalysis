package com.jshawn.netanalysis.profile;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;

@Component
public class TokenService {

	private final Map<String, String> hashes = new HashMap<String, String>();
	
	
	public String getHash() {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");  
//		LocalDateTime now = LocalDateTime.now(); 
		LocalDateTime now = LocalDateTime.now().plusHours(12);
		String timeStamp = dtf.format(now);
		
        String hash = UUID.randomUUID().toString().replace("-", ""); 
        hashes.put(hash, timeStamp);
        
        return hash;
    }
	
	public void invalidateHash(String hash) {
        hashes.remove(hash);
    }

    public boolean checkHash(String hash) {
    	
        return hashes.containsKey(hash);
    }
    
    public boolean checkExpiration(String hash) throws ParseException {
    	
    	String expirytimeStamp = hashes.get(hash);
    	
    	DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    	LocalDateTime now = LocalDateTime.now(); 
    	String nowtimeStamp = dtf.format(now);
    	
    	SimpleDateFormat sdformat = new SimpleDateFormat("yyyyMMddHHmmss");
    	Date nowData = sdformat.parse(nowtimeStamp);
        Date expirayDate = sdformat.parse(expirytimeStamp);
        
        //expirayDate occurs after nowData will be true
        return expirayDate.compareTo(nowData) > 0 || expirayDate.compareTo(nowData) == 0;
    }
    
}