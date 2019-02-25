package project1;

import java.io.*;
import java.net.*;
import java.util.*;
import java.net.Socket;
import java.net.*; 
import java.io.*; 

public class ftp_server {
    
	 
	    //initialize socket and input stream 
	    private Socket          socket   = null; 
	    private ServerSocket    server   = null; 
	    private DataInputStream in       =  null; 
	  
	    static final int port = 406;
	    
	    
	    /* server file path */
	    static final String filePath ="." + File.separator +
	        "server_files" + File.separator;
	    static final String EOF = "!EOF!";

	    // constructor with port 
	    public ftp_server() 
	    { 
	        // starts server and waits for a connection 
	        try
	        { 
	            server = new ServerSocket(port); 
	            System.out.println("Server started"); 
	  
	            System.out.println("Waiting for a client ..."); 
	  
	            socket = server.accept(); 
	            System.out.println("Client accepted"); 
	  
	            // takes input from the client socket 
	            in = new DataInputStream( 
	                new BufferedInputStream(socket.getInputStream())); 
	  
	            String line = ""; 
	  
	            // reads message from client until "Over" is sent 
	            while (!line.equals("CONNECT")) 
	            { 
	                try
	                { 
	                    line = in.readUTF(); 
	                    System.out.println(line); 
	  
	                } 
	                catch(IOException i) 
	                { 
	                    System.out.println(i); 
	                } 
	            } 
	            System.out.println("Closing connection"); 
	  
	            // close connection 
	            socket.close(); 
	            in.close(); 
	        } 
	        catch(IOException i) 
	        { 
	            System.out.println(i); 
	        } 
	    } 
	    public void list(int port , Socket connection) throws IOException {
	    	//socket for data transfer
	    	Socket dataSocket = new Socket(connection.getInetAddress(),port);
	    	
	    	 DataOutputStream dataOutToClient =

	                 new DataOutputStream(dataSocket.getOutputStream());
	    	 
	    	 // store files in array
	    	 File folder = new File(filePath);
	    	 File[] fileList = folder.listFiles();

	    	// iterate through each file and print name to output stream

	         for (File myFile: fileList){

	             // only shows files, not directories

	             if (myFile.isFile()) {

	                 dataOutToClient.writeUTF(myFile.getName());

	             }

	         }

	         // end transaction

	         dataOutToClient.writeUTF(EOF);
	         dataSocket.close();
	         System.out.println("List Data Socket closed");
	         
	    }
	    public static void main(String args[]) 
	    { 
	    	ftp_server server = new ftp_server(); 
	    	while(true) {
	    		
	    		
	    		
	    		
	    	}
	    } 
	} 