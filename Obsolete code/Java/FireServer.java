import java.net.*;
import java.io.*;

public class FireServer {
    public static void main(String[] args) throws IOException {
    	
        
    	//Set up the socket
    	ServerSocket serverSocket = null;
    	try {
    		serverSocket = new ServerSocket(4500);
    	} 
    	catch (IOException e) {
    		System.err.println("Could not listen on port: 4500.");
    		System.exit(1);
    	}
    	System.out.println("Fire server is up and waiting");

    	//Wait for client connection
    	Socket clientSocket = null;
    	try {
    		clientSocket = serverSocket.accept();
    	} 
    	catch (IOException e) {
    		System.err.println("Server socket failed");
    		System.exit(1);
    	}	
    	System.out.println("Connection accepted, transferring file...");

    	
    	//File file = new File("C:/Users/Shiyue/Documents/FYP/Fire data/AJ.json"); 
    	//byte size [] = new byte[(int) file.length()];
    	
    	byte size [] = new byte[12726];//check the getByte class for '12726'

    	//Read file from location
    	FileInputStream fireInput = new FileInputStream("C:/Users/Shiyue/Documents/FYP/Fire data/australia_january.json");
    	fireInput.read(size,0,size.length); 

    	//Convert to string and send to the client
    	OutputStream outputLine = clientSocket.getOutputStream();
    	outputLine.write(size,0,size.length);
    	System.out.println("File transferred successfully");

    	outputLine.flush();
    	fireInput.close();
    	clientSocket.close();
    	serverSocket.close();
    }
    
    /**
    public long getByte(long bSize) {
    	File file = new File("C:/Users/Shiyue/Documents/FYP/Fire data/australia_january.json"); 
	    //Declare byte size of file
	    bSize = file.length();
	    
	    return bSize;
    }
    **/
}
