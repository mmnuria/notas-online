package helpers;
import java.io.File;

public class FileSystem {

	  public boolean exists(String route) {
	    return new File(route).exists();
	  }
	  
	  public void createDirectory(String route) throws Exception {
		  if(!new File(route).mkdirs()) {
			  throw new Exception ("unable to create folder");
		  }
	  }
	  public void createFile(String route, String name) throws Exception {
		  if(!exists(route)) {
			  throw new Exception ("unable to find folder");
		  }
		  
		  new File(route + "/" + name).createNewFile();
	  }

}
