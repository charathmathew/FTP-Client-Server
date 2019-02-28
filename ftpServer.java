import java.io.*;
import java.util.*;
import java.nio.file.*;
import java.net.*;


public class ftpServer{

  private ServerSocket serverSocket = null;
  private Socket clientSocket = null;
  private final int PORT = 3000;
  private DataInputStream in = null;
  private DataOutputStream out = null;


  public ftpServer(){

    try{
      serverSocket = new ServerSocket(PORT);
      System.out.println("Listening for clients on port 3000...");

      clientSocket = serverSocket.accept();
      System.out.println("Connection Established...");

      /*Path path = FileSystems.getDefault().getPath(".");
      System.out.println("Current Working Directoy: " + path);

      Path aPath = FileSystems.getDefault().getPath("").toAbsolutePath();
      System.out.println("Absolute Path of CWD: " + aPath);*/

      in = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));
      out = new DataOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()));

    }
    catch(Exception e){
      System.out.println(e);
    }

  }

  public void sendFile(String filename){
    //String path = "/home/mathew/Documents/CS/457/FTP-Client-Server/sampleText.txt";
    String path = FileSystems.getDefault().getPath("").toAbsolutePath() + "/serverFiles/" + filename;

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

  public void receiveFile(){
      byte[] buffer = new byte[2000];
      try{
        InputStream is = clientSocket.getInputStream();
        String path = "/home/mathew/Documents/CS/457/serverFiles/clientFile1.txt";
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
    String path =  FileSystems.getDefault().getPath("").toAbsolutePath() + "/serverFiles/";
    File dir = new File(path);
    //Array of all files in dir
    File[] listOfFiles = dir.listFiles();

    System.out.println("----Server Files----");
    for(File file : listOfFiles){
      if (file.isFile()){
        System.out.println(file.getName());
      }
    }
  }

  public void displayMenu(){

    System.out.println("----Welcome to the FTP Server----");
    System.out.println("1. List Files  \t2. Retrieve Files");
    System.out.println("3. Store Files \t4. Terminate Connection");
    System.out.println("Enter a command: ");

  }

  public void retrieve(){
      sendFile("sample1.txt");
  }

  public void store(){
    System.out.println("Store a file to the server");
  }

  public static void main(String args[]){
    ftpServer server = new ftpServer();
    server.displayMenu();
    server.listFiles();
    server.sendFile("sample1.txt");
    server.receiveFile();
  }

}
