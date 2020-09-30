import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class FireClient {
    public static void main(String[] args) throws IOException {

        //Set up the socket
        Socket clientSocket = null;

        try {
            clientSocket = new Socket("localhost", 4500);
        } 
        catch (UnknownHostException e) {
            System.err.println("Can't find host");
            System.exit(1);
        } 
        catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to: 4500.");
            System.exit(1);
        }
    
        //File file = new File("C:/Users/Shiyue/AndroidStudioProjects/WildfireDetection/app/src/main/assets/AJ.json");   
        //byte size [] = new byte[(int) file.length()];
        
        byte size [] = new byte[12726];//check the getByte class for '12726'
        
      //Receive the file sent by the server
        InputStream inputLine = clientSocket.getInputStream();
        
      //Read from server
        inputLine.read(size, 0, size.length);
        
      //Store file in new location
        FileOutputStream fireOut = new FileOutputStream("C:/Users/Shiyue/AndroidStudioProjects/WildfireDetection/app/src/main/assets/australia_january.json");
        fireOut.write(size,0,size.length);
        System.out.println("File received from server");
        
        fireOut.close();   
        clientSocket.close();
    }
}