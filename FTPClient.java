import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.lang.*;

class FTPClient {

     
    static final String EOF = "!EOF!";

    static final String clientFilePath = "." + File.separator + "client_files" + File.separator;

   
    public static void main(String argv[]) throws Exception {
        
        String reader;

        boolean connecting = true;
        boolean connected = true;

    
        Socket dataSocket = null;

        System.out.println( 
                "\nLIST " + "\nRETR <filename> " + "\nSTOR <filename> " +  "\nQUIT");
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

        
        while (connected) {
        	String [] command = new String [3];
			reader = inFromUser.readLine();
            command = reader.split(" ");
           
            
            String connect = command[0].toUpperCase();
            if(connect.equals("CONNECT") && command.length==3){

              
            	String IPaddress = command[1];
				int port = Integer.parseInt(command[2]);


                Socket ControlSocket;
                try{
                 
                    ControlSocket = new Socket(IPaddress, port);

                    
                    DataOutputStream controlOut = new DataOutputStream(ControlSocket.getOutputStream());
                    DataInputStream controlIn =new DataInputStream(new BufferedInputStream(ControlSocket.getInputStream()));
                   
                    System.out.println("Connected to Server.");
                    connecting = true;
                    
                    while(connecting){
                    	reader = inFromUser.readLine();
    					command = reader.split(" ");
    					String option = command[0].toUpperCase();
                        
    					switch(option.toUpperCase()){
    					
                            case "LIST":
                                list(port,reader,controlOut,dataSocket);
                                break;
                            
                            case "RETR":
                                retr(port,reader,controlOut,dataSocket);
                                break;
                            
                            case "STOR":
                                stor(port, reader, controlOut, controlIn, dataSocket);
                                break;
                            
                            case "QUIT":
                                connecting = false;
                                connected = false;
                                break;
                            
                            default:
                                System.out.println("Command is Invaild");
                        }

                    }
                } catch (Exception e){
                    System.out.println(e);
                    connecting = false;
                }
            } 
            
        }
    }
   
    private static void list(int port,String sentence,DataOutputStream controlOut,Socket dataSocket) throws Exception {
       
        int dPort = port + 2;
        ServerSocket welcomeData = new ServerSocket(dPort);
        System.out.println(clientFilePath);
     
        controlOut.writeBytes(dPort + " " + sentence + " " + '\n');

        dataSocket = welcomeData.accept();
        DataInputStream inData =
                new DataInputStream(
                        new BufferedInputStream(dataSocket.getInputStream()));
        try {
            
            String serverData = inData.readUTF();
            System.out.println("Files on server: \n" + serverData);

           
            while (!serverData.equals(EOF)) {
                serverData = inData.readUTF();
              
                if (!serverData.equals(EOF))
                    System.out.println(serverData);
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        welcomeData.close();
        dataSocket.close();
    }

    
    private static void retr(int port, String sentence, DataOutputStream controlOut, Socket dataSocket) throws Exception {
        
        int dPort = port + 2;
        ServerSocket welcomeData = new ServerSocket(dPort);
        controlOut.writeBytes(dPort + " " + sentence + " " + '\n');
        String[] getFileName = sentence.split(" ", 2);

        dataSocket = welcomeData.accept();
        
        DataInputStream inData =new DataInputStream( new BufferedInputStream(dataSocket.getInputStream()));
        StringBuffer stringBuffer = new StringBuffer();

        Path filePath = Paths.get(clientFilePath + getFileName[1]);

            String status = inData.readUTF().toString();
            System.out.println(status);
           
            
            if (status.equals("200 OK")) {
                System.out.println("Downloading");

        
                String line;
                while (!(line = inData.readUTF()).equals(EOF)) {
                    stringBuffer.append(line);
                }
                Files.write(filePath, stringBuffer.toString().getBytes());
                System.out.println("Download Done!");
            } 
        
        welcomeData.close();
        dataSocket.close();
       
    }

    private static void stor(int port, String sentence, DataOutputStream controlOut, DataInputStream controlIn, Socket dataSocket) throws Exception {

        int dPort = port + 2;
      
        	 String[] getFileName = sentence.split(" ", 2);
        // http://www.avajava.com/tutorials/lessons/
        File file = new File(clientFilePath + getFileName[1]);

        if (!file.exists() || file.isDirectory()) {
            System.out.println("No such file or directory");
            return;
        }

        ServerSocket welcomeData = new ServerSocket(dPort);
        controlOut.writeBytes(dPort + " " + sentence + " " + '\n');

       
        String serverFileCreationError = controlIn.readUTF().toString();
       
        
        dataSocket = welcomeData.accept();
        DataOutputStream dataControlOut = new DataOutputStream(dataSocket.getOutputStream());
        
        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        String line;

        
        try {
            while ((line = bufferedReader.readLine()) != null) {
                dataControlOut.writeUTF(line + System.getProperty("line.separator"));
            }
            dataControlOut.writeUTF(EOF);
            fileReader.close();
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }

        DataInputStream inFromServer = new DataInputStream(dataSocket.getInputStream());
        String status = inFromServer.readUTF().toString();

        if (status.equals("200 OK")) {
            System.out.println("File Uploaded!");
        } 
        
        inFromServer.close();
        welcomeData.close();
        dataControlOut.close();
        dataSocket.close();
      
        return;
    }

}