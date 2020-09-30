import java.io.File;
import java.io.IOException;

public class getByte {
	 public static void main(String[] args) throws IOException {
		 File file = new File("C:/Users/Shiyue/Documents/FYP/Fire data/australia_january.json"); 
		    //Declare byte size of file
		    long bSize = file.length();
		    System.out.println("Byte: " + bSize);
	 }
}
