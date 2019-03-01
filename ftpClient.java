import java.io.*;
import java.util.*;
import java.nio.file.*;
import java.net.*;

public class ftpClient{

  private Socket socket = null;
  private DataInputStream inFromServer = null;
  private DataOutputStream outToServer = null;
  private BufferedReader inFromUser = null;
  Scanner scanner = null;
  Scanner sc = null;

  public ftpClient(int port){

      try {
        socket = new Socket("localhost", 3000);

        System.out.println("Client Connected...");

        outToServer = new DataOutputStream(socket.getOutputStream());
        inFromServer = new DataInputStream(socket.getInputStream());
        inFromUser = new BufferedReader(new InputStreamReader(System.in));
        Scanner scanner = new Scanner(System.in);
        Scanner sc = new Scanner(socket.getInputStream());
      }
      catch(Exception e){
        System.out.println(e);
        System.out.println("Failure");
      }
  }

  public void receiveFile(){
      byte[] buffer = new byte[2000];
      try{
        InputStream is = socket.getInputStream();
        String path = FileSystems.getDefault().getPath("").toAbsolutePath() + "/client_files/sample1.txt";
        FileOutputStream fos = new FileOutputStream(path);
        is.read(buffer, 0, buffer.length);
        fos.write(buffer, 0, buffer.length);
      }
      catch(Exception e){
        System.out.println(e);
      }
  }
  public void sendFile(String filename){
    String path = FileSystems.getDefault().getPath("").toAbsolutePath() + "/client_files/" + filename;

    try{
      FileInputStream fis = new FileInputStream(path);
      File sendFile = new File(path);
      byte[] byteArray = new byte [(int) sendFile.length()];
      fis.read(byteArray, 0, byteArray.length);
      OutputStream os = socket.getOutputStream();
      os.write(byteArray, 0, byteArray.length);
    }
    catch(Exception e){
      System.out.println(e);
    }
  }

  public void listFiles(){
    String filename = "";
    int i = 0;
    try {
      while(i < inFromServer.readInt()){
        filename = inFromServer.readLine();
        System.out.println(filename);
        i++;
      }
    } catch(Exception e) {
      System.out.println(e);
    }
  }


  public static void main(String args[]){
    ftpClient client = new ftpClient(3000);
    String [] request = new String [3];
    String command = "";


    while(!command.toUpperCase().equals("QUIT")){

      try {
        System.out.println("Enter a command:");
        command = client.inFromUser.readLine();
        request = command.split("\\s");
        command = request[0];
        client.outToServer.writeBytes(command + "\n");
      } catch(Exception e) {
        System.out.println(e);
      }
      System.out.println("you entered: " + command.toUpperCase());
      if(command.toUpperCase().equals("LIST")){
        client.listFiles();
      }
      if(command.toUpperCase().equals("RETRIEVE")){
        try{
          System.out.println("Command - " + command + " Filename - " + request [1]);
          client.outToServer.writeBytes(request[1] + "\n");
        }catch(Exception e){
          System.out.println(e);
        }
      }
      if(command.toUpperCase().equals("STORE")){
        try {
          System.out.println("Command - " + command + " Filename - " + request [1]);
          client.outToServer.writeBytes(request[1] + "\n");
        } catch(Exception e) {
          System.out.println(e);
        }
      }
    }

    if(command.toUpperCase().equals("QUIT")){
      try{
        client.socket.close();
      }catch(Exception e){
        System.out.println(e);
      }
      System.out.println("Exiting FTP Client...");
      System.exit(1);
    }

    client.receiveFile();
    client.sendFile("clientFile1.txt");
    client.listFiles();
  }

}
