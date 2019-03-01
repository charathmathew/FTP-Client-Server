import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;


import java.net.*;
import java.io.*;

public class server{

	    private static final int port = 2000;

	    public static void main(String args[]) throws Exception
	    {
	    	  //Socket that waits for a request
	        ServerSocket firstSocket = new ServerSocket(port);
	        System.out.println("Waiting....");
	        // infinite loop to constantly service clients
	        while (true) {

	            System.out.println("Waiting for a Command");

	            //receive a connection from client
	            Socket connectionSocket = firstSocket.accept();

	            // handles individual ftp clientsls
	            FTPHandler handler = new FTPHandler(connectionSocket);

	            //Output Connection
	            System.out.println("Connection Successed");

	            // start thread
	            handler.start();
	        }
	    }
}

//Reference: https://www.youtube.com/watch?v=T0BEAGWGImU&t=1196s
//Allow class to handle multilple threads of execution
 class FTPHandler extends Thread{

    // server file path
    static String serverFilePath ="." + File.separator +
        "server_files" + File.separator;
	    static final String EOF = "!EOF!";

	    // connection port
	    int port;

	    boolean quit = false;

	    //tokenizer for fromClient
	    StringTokenizer tokens;

	    //first line of string
	    String firstLine;

	    //sent command from client
	    String clientCommand;

	    //name of specified file
	    String fileName;

	    //client data string
	    String clientSentence;

	    //string sent from client
	    String fromClient;

	    //receive a connection
		Socket connectionSocket;

		//Input reader
		BufferedReader FromClient;

		 public FTPHandler(Socket connectionSocket) throws Exception {
		        this.connectionSocket = connectionSocket;
		        //writes something client
		        DataOutputStream   ToClient =
		                new DataOutputStream(connectionSocket.getOutputStream());
		        //reads from client
		         FromClient = new BufferedReader(new
		                InputStreamReader(connectionSocket.getInputStream()));

		        System.out.println("FTP Client connected " + connectionSocket.getInetAddress() +
		                " Port:" + connectionSocket.getPort());
		    }

	    public void run() {

	    	 boolean quit = true;
				while(quit) {
					try {
						//reads what was typed from client
						fromClient = FromClient.readLine();

		                System.out.println("fromClient: " + fromClient);

		                //breaks user's command into tokens
		                tokens = new StringTokenizer(fromClient);

		                //first line is the port number
		                firstLine = tokens.nextToken();

		                port = Integer.parseInt(firstLine);

		                //second line is the command option
		                clientCommand = tokens.nextToken();

		                //handle each different command here

		                switch(clientCommand.toUpperCase()) {

		                case "LIST":

		                /* socket for data transfer*/
				        Socket dataSocket = new Socket(connectionSocket.getInetAddress(), port);

				        //reads data to send to client
				        DataOutputStream dataOutToClient =
				                new DataOutputStream(dataSocket.getOutputStream());


				        // store a list of files in an array
				        File folder = new File(serverFilePath);
				        File[] listOfFiles = folder.listFiles();

				        // loop through each file
				        for (File myFile: listOfFiles){
				            //checks if it is a file
				            if (myFile.isFile()) {

				            	//server reads the files and sends them to clients
				                dataOutToClient.writeUTF(myFile.getName());
				            }
				        }
				        //close
				        //dataOutToClient.writeUTF(EOF);
				        dataSocket.close();
				       break;
		                case"RETRIEVE":
		                	Socket dataSocket1 = new Socket(connectionSocket.getInetAddress(), port);
		                	DataOutputStream dataOutToClient1 =
					                new DataOutputStream(dataSocket1.getOutputStream());

		                	try{

		                		fileName = tokens.nextToken();
												serverFilePath = FileSystems.getDefault().getPath("").toAbsolutePath() + "/server_files/" + fileName;
			                	//Path filePath = Paths.get(serverFilePath + fileName);


			                	dataOutToClient1.writeBytes(port + " " + clientCommand + " " + '\n');
		                	      FileInputStream fis = new FileInputStream(serverFilePath);
		                	      File sendFile = new File(serverFilePath.toString());
		                	      byte[] byteArray = new byte [(int) sendFile.length()];
		                	      fis.read(byteArray, 0, byteArray.length);
		                	      OutputStream os = dataSocket1.getOutputStream();
		                	      os.write(byteArray, 0, byteArray.length);
		                	    }
		                	    catch(Exception e){
		                	      System.out.println(e);
		                	    }



		                	break;

		                case "STORE":
											byte[] buffer = new byte[6000];
											try{
												fileName = tokens.nextToken();
												InputStream is = connectionSocket.getInputStream();
												String path = FileSystems.getDefault().getPath("").toAbsolutePath() + "/server_files/" + fileName;
												FileOutputStream fos = new FileOutputStream(path);
												is.read(buffer, 0, buffer.length);
												fos.write(buffer, 0, buffer.length);
											}
											catch(Exception e){
												System.out.println("HERE");
											}
		                	break;
		                case"QUIT":
										System.exit(1);
		                	break;

				    }
	    					}catch(Exception e) {

					}
				}
	    }
 }
