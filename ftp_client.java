package project1;

import java.io.*;

import java.net.*;

import java.nio.file.Files;

import java.nio.file.Path;

import java.nio.file.Paths;

import java.util.*;

import java.lang.*;

public class ftp_client {
	
	
	

	public static void main(String argv[]) throws Exception {
		
		// initialize socket and input output streams 
		Socket Socket = null; 
		String reader;
		DataInputStream  input = null; 
		DataOutputStream out   = null; 
		String filePath ="."+ File.separator + "files" + File.separator;
		
		//Command Introduction
		System.out.println("Welcome to FTP server program for file transfer\n"
				+ "Please connect to server as follows\n"
				+ "CONNECT <server name/IP address> <server port>\n"
				+ "After conneting successfully\n"
				+ "Ues these commands below\n\n"
				+ "LIST :Returns a list of file in the current directory\n"
				+ "RETRIEVE <filename> :Allows a client to get a file\n"
				+ "STORE <filename> :Allows a client to send a file\n"
				+ "QUIT :Terminate the conrol connection\n"

				);

		System.out.print("Enter here:");
		
		
		//create object that 
		BufferedReader userInput = new BufferedReader( new InputStreamReader(System.in));
		boolean connected = true;
		boolean connecting = true;

		while (connecting) {
			String [] command = new String [3];
			reader = userInput.readLine();
			command = reader.split(" ");
			
//			System.out.println(command[0]);
//			System.out.println(command[1]);
//			System.out.println(command[2]);
			
			String connect = command[0].toUpperCase();
			//first string should be CONNECT to start 
			if(connect.equals("CONNECT") && command.length==3) {

				String IPaddress = command[1];
				int port = Integer.parseInt(command[2]);

				/*https://www.geeksforgeeks.org/socket-programming-in-java/*/
				//establish a connection
				try {
					
				
					Socket = new Socket(IPaddress,port);
					

					System.out.println("CONNECT"); 
					
					//send output to the socket
					out = new DataOutputStream(Socket.getOutputStream()); 
					//takes input from terminal 
					input  = new DataInputStream(System.in);

				

				while(connected) {

					reader = userInput.readLine();
					command = reader.split(" ");

					String option = command[1].toUpperCase();
					switch(option.toUpperCase()) {
					
					case "LIST":
						int port1 = port + 2;
						ServerSocket listData = new ServerSocket(port);
						
						System.out.println(filePath);
						
						  Socket = listData.accept();

					        DataInputStream inData =

					                new DataInputStream(

					                        new BufferedInputStream(Socket.getInputStream()));
					        
					        try {

					            /* first UTF line from server */
					            String serverData = inData.readUTF();
					            // start printing file list
					            
					            System.out.println("Files on server: \n" + serverData);


					            // while server doesn't pass EOF character

					            while (serverData !=null) {

					                // continue reading each line and printing file name

					                serverData = inData.readUTF();

					                // dont print end of file character

					                if (serverData !=null)

					                    System.out.println(serverData);
					            }

					        } catch (Exception e) {

					            System.out.println(e);

					        }
					        
					        
		  
					        
					        
						break;
					case "RETRIEVE":
						break;
					case "STORE":
						break;
					case "QUIT":
						break;
					default:
						System.out.println("Enter one of above commands again");
					}
				}
			}
			catch (Exception e) {
					
					System.out.println("Failed to connect to a server\n"
										+"Try again\n");
					connected =false;
			}
		}
			else if(connect.equals("QUIT")) {
				
				System.out.println("Thank you Bye");
				connected =false;				
			}
			
		}
	}
	
	
}