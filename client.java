
import java.io.*;

import java.net.*;

import java.nio.file.Files;

import java.nio.file.Path;

import java.nio.file.Paths;

import java.util.*;

import java.lang.*;

public class client {
	
	
	 static final String EOF = "!EOF!";
	 static String filePath ="."+ File.separator + "client_files" + File.separator;

	public static void main(String argv[]) throws Exception {
		
		// initialize socket and input output streams 
		
		String reader;
		//DataInputStream  inData = null; 
		DataOutputStream out   = null; 
		Socket dataSocket =null;
		
		
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
			command = reader.split("\\s");
			
//			System.out.println(command[0]);
//			System.out.println(command[1]);
//			System.out.println(command[2]);
			
			String connect = command[0].toUpperCase();
			//first string should be CONNECT to start 
			if(connect.equals("CONNECT") && command.length==3) {

				String IPaddress = command[1];
				int port = Integer.parseInt(command[2]);
				System.out.println(port);
				/*https://www.geeksforgeeks.org/socket-programming-in-java/*/
				//establish a connection
				try {
					
				
					 Socket connectSocket = new Socket(IPaddress,port);
					

					System.out.println("CONNECT"); 
					
					//send output to the socket
					out = new DataOutputStream(connectSocket.getOutputStream()); 
					  
					
						

				while(connected) {
					 
					reader = userInput.readLine();
					command = reader.split(" ");

					String option = command[0].toUpperCase();
					switch(option.toUpperCase()) {
					
					case "LIST":
						// Create server socket
				        
						int dPort = port + 2;
                        ServerSocket welcomeData = new ServerSocket(dPort);
                        System.out.println(filePath);
                        
                        // write user sentence to server
                        out.writeBytes(dPort + " " + reader + " " + '\n');

                        // instantiate dataSocket
                        dataSocket = welcomeData.accept();
                        DataInputStream inData =
                                new DataInputStream(
                                        new BufferedInputStream(dataSocket.getInputStream()));
                       
                            // first UTF line from server
                            String serverData = inData.readUTF();

                            // start printing file list
                            System.out.println("Files on server: \n" + serverData);

                           

                                // continue reading each line and printing file name
                                serverData = inData.readUTF();

                                // dont print end of file character
                            
                                    System.out.println(serverData);
                          

                        //welcomeData.close();
                        //dataSocket.close();
				        
						break;
					case "RETRIEVE":
						int dPort1 = port + 2;
						out.writeBytes(dPort1 + " " + reader + " " + '\n');
						
						Path filePath1 = Paths.get(filePath + command[1]);
						StringBuffer stringBuffer = new StringBuffer();
						byte[] buffer =new byte[2000];
						try{
							
					        InputStream is = dataSocket.getInputStream();
					        FileOutputStream fos = new FileOutputStream(filePath);
					        Files.write(filePath1, stringBuffer.toString().getBytes());
					        is.read(buffer, 0, buffer.length);
					        fos.write(buffer, 0, buffer.length);
					        
						}
					      catch(Exception e){
					        System.out.println(e);
					      }
						
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