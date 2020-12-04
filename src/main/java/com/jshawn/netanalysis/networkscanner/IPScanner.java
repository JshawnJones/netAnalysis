package com.jshawn.netanalysis.networkscanner;

import java.net.InetAddress;
import java.io.*;
import java.net.*;
import java.util.*;
import static java.lang.System.out;

public class IPScanner {
	
	public static void scanNetwork() throws SocketException, IOException {	
		//Note: for 192.168.3.x addresses 
		byte[] manualip = {(byte)192, (byte)168, 3, 0};  
        

		for (int i = 1; i <= 254; i++) {  
        	final int j = i;
        	// new thread for parallel execution
        	Thread scannerThread = new Thread(new Runnable() {   
	            public void run() {
	            	try {
	            		manualip[3] = (byte) j;  
			            InetAddress address = InetAddress.getByAddress(manualip);  
			            String output = address.toString().substring(1);
			            
			            if (address.isReachable(1000)) {
			                System.out.println("online: " + output);

			            } 
	            	} catch (Exception e) {
	                    e.printStackTrace();
	                }
	            }
	        });     
        	
        	scannerThread.start(); 

        } 
		
	}
	
	
	public static void displayInterfaceInformation(NetworkInterface netint) throws SocketException {
        out.printf("Display name: %s\n", netint.getDisplayName());
        out.printf("Name: %s\n", netint.getName());
        Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
        for (InetAddress inetAddress : Collections.list(inetAddresses)) {
            out.printf("InetAddress: %s\n", inetAddress);
        }
        out.printf("\n");
     }

	public static void netstat() throws IOException {
		ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", "netstat -rn");
	    builder.redirectErrorStream(true);
	    Process p = builder.start();
	    BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
	    String line;
	    while (true) {
	        line = r.readLine();
	        if (line == null) { break; }
	        System.out.println(line);
	    }
	}

}