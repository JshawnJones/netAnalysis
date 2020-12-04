package com.jshawn.netanalysis.networkscanner;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import java.util.logging.Level;
import java.util.logging.Logger;

@Controller
public class SignalController<T> {
	
	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;
	
	@Autowired
	private SessionRegistry sessionRegistry;
	
	@MessageMapping("/status")
	public void ScannerResult(@Payload Message<T> msg, 
			SimpMessageHeaderAccessor headAccessor,
			@Header("simpSessionId") String sessionId) {
		
        ExecutorService executor = Executors.newFixedThreadPool(254);
        
        String rawip = headAccessor.getSessionAttributes().get("ip").toString();
        sessionRegistry.registerNewSession(rawip, headAccessor.getUser().getName());


        
        byte[] ip = {(byte)192, (byte)168, 3, 0};
        for (int i = 1; i <= 254; i++) {  
        	final int j = i;
        	
        	executor.submit(new Callable<String>() {

                @Override
                public String call() throws IOException {
                	ip[3] = (byte) j;
                	InetAddress address;
                	String output = "";
					try {
						address = InetAddress.getByAddress(ip);
						
						if (address.isReachable(9000)) {
							output = address.toString().substring(1);
							Logger ipLog = Logger.getLogger( 
				            		SignalController.class.getName());
							
							ipLog.log(Level.INFO, "online: " + output);
							
							if(sessionRegistry.getSessionInformation(output) == null) {
								sendtoClient(output, sessionId);
							} else {
								sendtoClient(output + "; user: "  + sessionRegistry.getSessionInformation(output).getPrincipal().toString(), sessionId);
							}
							
							

			            } 
					} catch (UnknownHostException e) {
						Logger logger = Logger.getLogger( 
			            		SignalController.class.getName());
						logger.log(Level.SEVERE, "UnknownHostException was thrown", e);
						
					}  
		            
					return output;
                }
            });
        	
        }

        executor.shutdown();

	}

	@RequestMapping(path="/ipresults", method=RequestMethod.POST)
	public void sendtoClient(String input, String sessionId) {

		simpMessagingTemplate.convertAndSend("/network/ipresults" + sessionId, input);

	}
  

	
	
}