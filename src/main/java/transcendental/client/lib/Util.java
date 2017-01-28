package transcendental.client.lib;

import javax.xml.crypto.Data;
import java.awt.datatransfer.DataFlavor;

/**
 * Created by markus on 28.01.17.
 */
public class Util {

	public static byte[] serializeFlavors(DataFlavor[] flavors){
		//TODO
		return new byte[]{};
	}
	public static DataFlavor[] deserializeFlavors(byte[] data){
		//TODO
		return new DataFlavor[]{};
	}


	public static byte[] serializeFlavor(DataFlavor flavor){
		//TODO
		return new byte[]{};
	}
	public static DataFlavor deserializeFlavor(byte[]data){
		//TODO
		DataFlavor df= null;
		try {
			df = new DataFlavor("text/html");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return df;
	}

	//TODO de/serialize actual Data
}
