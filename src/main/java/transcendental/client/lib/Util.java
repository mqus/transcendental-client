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


	public static void ohMyGodUtf8IsNotSupportedWhatShouldIDo(Throwable e) throws Fit{
		e.printStackTrace();
		throw new Fit("UTF8 is not supported on this System, I can't live on.", e);
	}
}

class Fit extends Error{
	/**
	 * Constructs a new error with the specified detail message and
	 * cause.  <p>Note that the detail message associated with
	 * {@code cause} is <i>not</i> automatically incorporated in
	 * this error's detail message.
	 *
	 * @param message the detail message (which is saved for later retrieval
	 *                by the {@link #getMessage()} method).
	 * @param cause   the cause (which is saved for later retrieval by the
	 *                {@link #getCause()} method).  (A {@code null} value is
	 *                permitted, and indicates that the cause is nonexistent or
	 *                unknown.)
	 * @since 1.4
	 */
	public Fit(String message, Throwable cause) {
		super(message, cause);
	}
}
