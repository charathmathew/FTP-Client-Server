import java.io.*;
import java.util.*;
import java.nio.file.*;
import java.net.*;

public class ftpClient{

  private Socket socket = null;
  private DataInputStream in = null;
  private DataOutputStream out = null;

  public ftpClient(int port){

      try {
        socket = new Socket("localhost", 3000);

        System.out.println("Client Connected...");

        out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        in = new DataInputStream(System.in);
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
        String path = "/home/mathew/Documents/CS/457/sample1.txt";
        FileOutputStream fos = new FileOutputStream(path);
        is.read(buffer, 0, buffer.length);
        fos.write(buffer, 0, buffer.length);
      }
      catch(Exception e){
        System.out.println(e);
      }
  }
  public void sendFile(String filename){
    String path = FileSystems.getDefault().getPath("").toAbsolutePath() + "/clientFiles/" + filename;

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

  public static void main(String args[]){
    ftpClient client = new ftpClient(3000);
    client.receiveFile();
    client.sendFile("clientFile1.txt");
  }

}
