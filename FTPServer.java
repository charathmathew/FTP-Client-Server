import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


class FTPServer {

    public static void main(String argv[]) throws Exception {
       
        ServerSocket welcomeSocket = new ServerSocket(2000);
   
        while (true) {
        	System.out.println("Waiting");
            Socket connectionSocket = welcomeSocket.accept();      
            RequestHandler handler = new RequestHandler (connectionSocket);          
            System.out.println("Serving connection " + connectionSocket.getInetAddress());           
            handler.start();
        }
    }
}

//https://www.youtube.com/watch?v=T0BEAGWGImU&t=686s
class RequestHandler extends Thread {
   
    static final String EOF = "!EOF!";
    static final String serverFilePath ="." + File.separator + "server_files" + File.separator;
   
    String fromClient;    
    StringTokenizer tokens;
    String firstLine;
    String clientCommand; 
    String fileName;
    String clientSentence;
    boolean quit = false;

    DataOutputStream outToClient;
    BufferedReader inFromClient;
    int port;
    Socket connectionSocket;

    
    public RequestHandler(Socket connectionSocket) throws Exception {
        this.connectionSocket = connectionSocket;
        
         outToClient = new DataOutputStream(connectionSocket.getOutputStream());
        inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
        
        System.out.println("Client connected " + connectionSocket.getInetAddress());
    }

    public void run() {
        do {
            try {
       	
                fromClient = inFromClient.readLine();
                System.out.println("fromClient: " + fromClient);


                tokens = new StringTokenizer(fromClient);
                
                firstLine = tokens.nextToken();
                port = Integer.parseInt(firstLine);             
                clientCommand = tokens.nextToken();
                switch (clientCommand.toUpperCase()) {
               
                case "LIST":
                    listCommand(connectionSocket, port);
                    break;
                    
                case "RETR":
                    retrCommand(connectionSocket, port);
                    break;
                case "STOR":
                    storCommand(connectionSocket, port);
                    break;
                case "QUIT":
                    quit = true;
                default:
                    System.out.println("Command is Invaild");
                }
            } catch (Exception e) {
                System.out.println(e);
                System.out.println("Connection Lost..");
                //stop loop, might change later
                quit = true;
            }
        } while (quit == false);
    }

  
    private void listCommand(Socket connectionSocket, int port) throws Exception {
        
        Socket dataSocket = new Socket(connectionSocket.getInetAddress(), port);
        DataOutputStream dataOutToClient =
                new DataOutputStream(dataSocket.getOutputStream());

        File folder = new File(serverFilePath);
        File[] listOfFiles = folder.listFiles();

        
        for (File myFile: listOfFiles){
            
            if (myFile.isFile()) {
                dataOutToClient.writeUTF(myFile.getName());
            }
        }
        
        dataOutToClient.writeUTF(EOF);
        dataSocket.close();
        System.out.println("List Data Socket closed");
    }

    private void retrCommand(Socket connectionSocket, int port) throws Exception {

        
        Socket dataSocket = new Socket(connectionSocket.getInetAddress(), port);
        DataOutputStream dataOutToClient =
                new DataOutputStream(dataSocket.getOutputStream());

       
        
            fileName = tokens.nextToken().toString();

        Path filepath = Paths.get(serverFilePath + fileName);
        File folders = new File(filepath.toString());

       
        if (folders.exists()) {
         
            dataOutToClient.writeUTF("200 OK");
        
            FileReader fileReader = new FileReader(folders);
            BufferedReader buffReader = new BufferedReader(fileReader);

         
            try {
                while ((clientSentence = buffReader.readLine()) != null) {
                    dataOutToClient.writeUTF(clientSentence + System.getProperty("line.separator"));
                }

                dataOutToClient.writeUTF(EOF);
                dataOutToClient.close();

              
                fileReader.close();
                System.out.println("File Downloaded Successfully!");

            } catch (IOException e) {
                e.printStackTrace();
            }
        } 
 
        dataSocket.close(); 
    }

    private void storCommand(Socket connectionSocket, int port) throws Exception {

        try {
            fileName = tokens.nextToken();
        } catch (Exception e ) {}
        Path filePath = Paths.get(serverFilePath + fileName);

        Files.deleteIfExists(filePath);
        outToClient.writeUTF("200 OK");

        Socket dataSocket = new Socket(connectionSocket.getInetAddress(), port);

        DataInputStream dataInFromClient = new DataInputStream(new BufferedInputStream(dataSocket.getInputStream()));
        DataOutputStream dataOutToClient =  new DataOutputStream(dataSocket.getOutputStream());

        StringBuffer stringBuffer = new StringBuffer();

        try {
            String line;
            while (!(line = dataInFromClient.readUTF()).equals(EOF)) {
                stringBuffer.append(line);
            }
            System.out.println("File " + fileName + " recieved from client.");
            Files.write(filePath, stringBuffer.toString().getBytes());

        } catch (IOException e) {
            e.printStackTrace();
        }

        File file = new File(filePath.toString());
        
        
            dataOutToClient.writeUTF("200 OK");
           
        dataInFromClient.close();
        dataOutToClient.close();
        dataSocket.close();
        

        return;
    }
}