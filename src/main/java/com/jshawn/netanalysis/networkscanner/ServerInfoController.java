package com.jshawn.netanalysis.networkscanner;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.VirtualMemory;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;
import oshi.software.os.OSService;
import oshi.software.os.OperatingSystem;
import oshi.util.FormatUtil;
import oshi.util.Util;

import java.util.logging.Level;
import java.util.logging.Logger;

@Controller
public class ServerInfoController<T> {
	
	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;
	
	@Autowired
	private SessionRegistry sessionRegistry;
	
	@MessageMapping("/server")
	public void ScannerResult(SignalMessage message,
			@Payload Message<T> msg, 
			SimpMessageHeaderAccessor headAccessor,
			@Header("simpSessionId") String sessionId) {
		
        //get session by ip
        String rawip = headAccessor.getSessionAttributes().get("ip").toString();
        
        //register session into session registry
        sessionRegistry.registerNewSession(rawip, headAccessor.getUser().getName());
        
        if(message.getStatus().equals("OK")) {
        	sessionRegistry.registerNewSession(sessionId, headAccessor.getUser().getName());
        	sessionRegistry.getSessionInformation(sessionId);
        	
        	
        } else if(message.getStatus().equals("STOP")){
        	sessionRegistry.removeSessionInformation(sessionId);
        }
        

		//oshi computer monitor system
		SystemInfo si = new SystemInfo();

		//Cpu load
		realtimeCPUMonitor(si, sessionId, sessionRegistry, headAccessor.getUser().getName(),
				sessionRegistry.getSessionInformation(sessionId));		

	}
	
	@MessageMapping("/resources")
	public void ResourcesResult(SignalMessage message,
			@Payload Message<T> msg, 
			SimpMessageHeaderAccessor headAccessor,
			@Header("simpSessionId") String sessionId) {
		
        //get session by ip
        String rawip = headAccessor.getSessionAttributes().get("ip").toString();
        
        //register session into session registry
        sessionRegistry.registerNewSession(rawip, headAccessor.getUser().getName());
        
        if(message.getStatus().equals("OK")) {
        	sessionRegistry.registerNewSession(sessionId, headAccessor.getUser().getName());
        	sessionRegistry.getSessionInformation(sessionId);
        	
        	
        } else if(message.getStatus().equals("STOP")){
        	sessionRegistry.removeSessionInformation(sessionId);
        }
        

		//oshi computer monitor system
		SystemInfo si = new SystemInfo();

		//Cpu load
		realtimeResourcesMonitor(si, sessionId, sessionRegistry, headAccessor.getUser().getName(),
				sessionRegistry.getSessionInformation(sessionId));		

	}
	
	@RequestMapping(path="/cpuresults", method=RequestMethod.POST)
	public void sendServertoClient(String input, String sessionId)  {
		simpMessagingTemplate.convertAndSend("/network/cpuresults" + sessionId, input);
	}

	@RequestMapping(path="/reresults", method=RequestMethod.POST)
	public void sendResourcestoClient(String input, String sessionId)  {
		simpMessagingTemplate.convertAndSend("/network/resourcesresults" + sessionId, input);
	}
	
	private void realtimeCPUMonitor(SystemInfo si, String sessionId, 
			SessionRegistry sessionRegistry, String principalName,
			SessionInformation sessionInfo) {
		
		HardwareAbstractionLayer hal = si.getHardware();
		OperatingSystem os = si.getOperatingSystem();
		CentralProcessor processor = hal.getProcessor();
		
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
		long[] prevTicks = processor.getSystemCpuLoadTicks();
		long[][] prevProcTicks = processor.getProcessorCpuLoadTicks();
		
        //initialize log
        Logger serverLog = Logger.getLogger( 
        		ServerInfoController.class.getName());
		
        boolean stopTrigger = true;
        while(stopTrigger) {
        	
        	
	        //check if the sesson still exists in the session registry
	        List<SessionInformation> sessionList = sessionRegistry.getAllSessions(principalName, true);
	        if(!sessionList.contains(sessionInfo)) {
	        	stopTrigger = false;
	        } else {
	        	//one second interval
	    		Util.sleep(1000);
	        	//create new string
	    		StringBuilder resultsContainer = new StringBuilder("{");	
	    		//get cpu usage percentage
	    		StringBuilder cpuResults = cpuLoad(resultsContainer, serverLog, dtf,
	        			processor, prevTicks, prevProcTicks);
	        	//get memory usage percentage
	    		StringBuilder memoryResults = memoryLoad(cpuResults, hal.getMemory());
	    		//get storage usage percentage
	    		StringBuilder storageResults = storageLoad(memoryResults, os.getFileSystem());

	    		storageResults.append("}");
	    		
		        //send results back to client
		        sendServertoClient(storageResults.toString(), sessionId);
	        }
        	
		}

	}
	
	private void realtimeResourcesMonitor(SystemInfo si, String sessionId, 
			SessionRegistry sessionRegistry, String principalName,
			SessionInformation sessionInfo) {
		
		HardwareAbstractionLayer hal = si.getHardware();
		OperatingSystem os = si.getOperatingSystem();
		CentralProcessor processor = hal.getProcessor();
		
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
		long[] prevTicks = processor.getSystemCpuLoadTicks();
		long[][] prevProcTicks = processor.getProcessorCpuLoadTicks();
		
        //initialize log
        Logger serverLog = Logger.getLogger( 
        		ServerInfoController.class.getName());
		
        boolean stopTrigger = true;

    	//create new string
		StringBuilder resultsContainer = new StringBuilder("{");	
		
		resultsContainer.append("\"bufferindex\":\"test\"");

		//get running services
		StringBuilder servicesResults = servicesLoad(resultsContainer, os);

		servicesResults.append("}");
		
        //send results back to client
		sendResourcestoClient(servicesResults.toString(), sessionId);


	}
	
	//get the percentage load of cpu
	private static StringBuilder cpuLoad(StringBuilder cpuResults, Logger serverLog, DateTimeFormatter dtf,
			CentralProcessor processor, long[] prevTicks,
			long[][] prevProcTicks) {
		
		
		//get current time
		LocalDateTime now = LocalDateTime.now();  
		String timeStamp = dtf.format(now);
		
		//append to string builder
		cpuResults.append(String.format("\"%1$s\":\"%2$s\"", "time", timeStamp));
		cpuResults.append(", ");
		
		//cpu usage
		cpuResults.append(String.format("\"CPUload\":\"%.1f\"", processor.getSystemCpuLoadBetweenTicks(prevTicks) * 100));
		serverLog.log(Level.INFO, String.format("CPU load: %.1f%%", processor.getSystemCpuLoadBetweenTicks(prevTicks) * 100).toString());
		
		// usage per cpu core
        double[] load = processor.getProcessorCpuLoadBetweenTicks(prevProcTicks);     
        int coreCounter = 0;
        
        for (double avg : load) {
        	//core count
        	coreCounter = coreCounter + 1;
        	cpuResults.append(", ");
            cpuResults.append(String.format("\"Core"+ coreCounter + "\":\"%.1f\"", avg * 100));
            
        }
        
        cpuResults.append(", ");
        cpuResults.append("\"NumOfCores\":\""+coreCounter+"\"");
        
		return cpuResults;
	}
	
	//get percentage memory usage
	private static StringBuilder memoryLoad(StringBuilder cpuResults, GlobalMemory memory) {
		//Physical memory usage
		cpuResults.append(", ");
		String physicalmem = memory.toString().replace("Available: ", "");
		cpuResults.append(String.format("\"PhysicalMemory\":\"%1$s\"", physicalmem));
		
		//Virtual memory usage
		VirtualMemory vm = memory.getVirtualMemory();
		cpuResults.append(", ");
		String virtualmem = vm.toString().split(",")[1].replace(" Virtual Memory In Use/Max=", "");
		cpuResults.append(String.format("\"VirtualMemory\":\"%1$s\"", virtualmem));
		
		return cpuResults;
	}

	//get percentage storage usage
	private static StringBuilder storageLoad(StringBuilder cpuResults, FileSystem fileSystem) {
		
		int diskCounter = 0;
        for (OSFileStore fs : fileSystem.getFileStores()) {
        	        	
        	//if(fs.getType().equals("NTFS")) {
        		long usable = fs.getUsableSpace();
                long total = fs.getTotalSpace();
                
                diskCounter = diskCounter + 1;
                
                cpuResults.append(", ");
        		cpuResults.append(String.format("\"diskName"+diskCounter+"\":\"%1$s\"", fs.getName()));
        		cpuResults.append(", ");
        		cpuResults.append(String.format("\"diskDescription"+diskCounter+"\":\"%1$s\"", fs.getDescription().isEmpty() ? "file system" : fs.getDescription()));
        		cpuResults.append(", ");
        		cpuResults.append(String.format("\"diskUsable"+diskCounter+"\":\"%1$s\"", FormatUtil.formatBytes(usable)));
        		cpuResults.append(", ");
        		cpuResults.append(String.format("\"diskTotal"+diskCounter+"\":\"%1$s\"", FormatUtil.formatBytes(fs.getTotalSpace())));
        		cpuResults.append(", ");
        		cpuResults.append(String.format("\"diskPercentage"+diskCounter+"\":\"%1$s\"", 100d * usable / total));
        		
        	//}
            
        }
        cpuResults.append(", ");
		cpuResults.append(String.format("\"NumberofDisks\":\"%1$s\"", diskCounter));
		
		return cpuResults;
	}
	

	

	//get percentage memory usage
	private static StringBuilder servicesLoad(StringBuilder cpuResults, OperatingSystem os) {


        for (OSService s : os.getServices()) {
            if (s.getState().equals(OSService.State.RUNNING) ) {           	
            	
        		cpuResults.append(", ");
        		cpuResults.append(String.format("\""+s.getProcessID()+"\":\"%1$s\"", s.getName()));
            }
        }
		
		return cpuResults;
	}
	
}