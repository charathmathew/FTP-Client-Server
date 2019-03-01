import java.io.*;
import java.util.*;
import java.nio.file.*;
import java.net.*;


public class ftpServer{

  private ServerSocket serverSocket = null;
  private Socket clientSocket = null;
  private final int PORT = 3000;
  private BufferedReader inFromClient = null;
  private DataOutputStream outToClient = null;
  private Scanner scanner = null;

  public ftpServer(){

    try{
      serverSocket = new ServerSocket(PORT);
      System.out.println("Listening for clients on port 3000...");

      clientSocket = serverSocket.accept();
      System.out.println("Connection Established...");

      inFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
      outToClient = new DataOutputStream(clientSocket.getOutputStream());
      Scanner scanner = new Scanner(clientSocket.getInputStream());
      new PrintStream(clientSocket.getOutputStream());

    }
    catch(Exception e){
      System.out.println(e);
    }

  }

  public void sendFile(String filename){
    //String path = "/home/mathew/Documents/CS/457/FTP-Client-Server/sampleText.txt";
    String path = FileSystems.getDefault().getPath("").toAbsolutePath() + "/server_files/" + filename;

    try{
      FileInputStream fis = new FileInputStream(path);
      File sendFile = new File(path);
      byte[] byteArray = new byte [(int) sendFile.length()];
      fis.read(byteArray, 0, byteArray.length);
      OutputStream os = clientSocket.getOutputStream();
      os.write(byteArray, 0, byteArray.length);
    }
    catch(Exception e){
      System.out.println(e);
    }
  }

  public void receiveFile(String filename){
      byte[] buffer = new byte[2000];
      try{
        InputStream is = clientSocket.getInputStream();
        String path = FileSystems.getDefault().getPath("").toAbsolutePath() + "/server_files/" + filename;
        FileOutputStream fos = new FileOutputStream(path);
        is.read(buffer, 0, buffer.length);
        fos.write(buffer, 0, buffer.length);
      }
      catch(Exception e){
        System.out.println(e);
      }
  }

  public void listFiles(){

    //path of the server files directory
    String path =  FileSystems.getDefault().getPath("").toAbsolutePath() + "/server_files/";
    File dir = new File(path);
    //Array of all files in dir
    File[] listOfFiles = dir.listFiles();
    try{
      //System.out.println("----Server Files----");
      outToClient.writeInt(listOfFiles.length);
      for(File file : listOfFiles){
        if (file.isFile()){
          outToClient.writeBytes(file.getName()+"\n");
          System.out.println(file.getName());
        }
      }
      outToClient.close();
    }catch(Exception e){
      System.out.println(e);
    }
  }

  public void displayMenu(){

    System.out.println("----Welcome to the FTP Server----");
    System.out.println("1. List Files  \t2. Retrieve Files");
    System.out.println("3. Store Files \t4. Terminate Connection");
    System.out.println("Enter a command: ");

  }

  public void retrieve(String filename){
      sendFile(filename);
  }

  public void store(String filename){
      receiveFile(filename);
  }

  public static void main(String args[]){
    ftpServer server = new ftpServer();

    String command = "";
    String filename = "";
    while(!command.toUpperCase().equals("QUIT")){
      try {
        command = server.inFromClient.readLine();
        //System.out.println(command);
      } catch(Exception e) {
        System.out.println(e);
      }

      System.out.println("Client Request: " + command.toUpperCase());
      if(command.toUpperCase().equals("LIST")){
        //System.out.println("LIST");
        server.listFiles();
      }

      if(command.toUpperCase().equals("RETRIEVE")){
        try {
          filename = server.inFromClient.readLine();
          System.out.println("RETRIEVE " + filename);
          server.retrieve(filename);
        } catch(Exception e) {
          System.out.println(e);
        }
      }

      if(command.toUpperCase().equals("STORE")){
        try {
          filename = server.inFromClient.readLine();
          System.out.println("STORE " + filename);
          server.store(filename);
        } catch(Exception e) {
          System.out.println(e);
        }
      }
    }

    if(command.toUpperCase().equals("QUIT")){
      try{
        server.clientSocket.close();
        server.serverSocket.close();
      }catch(Exception e){
        System.out.println(e);
      }
      System.out.println("Exiting FTP Server...");
      System.exit(1);
    }
  }

}
