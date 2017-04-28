package transcendental.client.lib;

import org.apache.commons.codec.binary.StringUtils;

import javax.xml.crypto.Data;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.*;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.io.IOUtils;



/**
 * Created by markus on 28.01.17.
 */
public class Util {
	public static final int BUFFER_LIMIT=512*1024*1024;//512 MiB

	public static byte[] serializeFlavors(DataFlavor[] flavors){
		StringBuilder sb=new StringBuilder();
		HashSet<String> mimetypes=new HashSet<>();
		for(DataFlavor flavor : flavors) {
			//only take binary representable flavors
			if(!flavor.isRepresentationClassInputStream())continue;

			//TODO: mt or mime?  mime has class= and charset= parameter, mt is only type/subtype
			String mime=flavor.getMimeType();
			String mt=mime.split(";")[0];
			mimetypes.add(mime);
		}
		String out = join(mimetypes, "\n");
		byte[] bytes=null;
		try {
			bytes=out.getBytes("UTF-8");
		} catch(UnsupportedEncodingException e) {
			ohMyGodUtf8IsNotSupportedWhatShouldIDo(e);
		}
		return bytes;
	}
	public static DataFlavor[] deserializeFlavors(byte[] data){

		String mimeTypesString = "";
		try {
			mimeTypesString = new String(data, "UTF-8");
		} catch(UnsupportedEncodingException e) {
			ohMyGodUtf8IsNotSupportedWhatShouldIDo(e);
		}
		String[] mimeTypes = mimeTypesString.split("\n");

		List<DataFlavor> flavors = new Vector<>(mimeTypes.length);

		for(String mimeType : mimeTypes) {
			try {
				flavors.add(new DataFlavor(mimeType));
			} catch(ClassNotFoundException e) {
				//MAYBE silent
				e.printStackTrace();
			}
		}

		return flavors.toArray(new DataFlavor[0]);
	}


	public static byte[] serializeFlavor(DataFlavor flavor){
		//with split: get only mime type/subtype without params.
		String flavorString = flavor.getMimeType();//.split(";")[0];


		try {
			return flavorString.getBytes("UTF-8");
		} catch(UnsupportedEncodingException e) {
			ohMyGodUtf8IsNotSupportedWhatShouldIDo(e);
			return null;
		}

	}
	public static DataFlavor deserializeFlavor(byte[] data){
		String mimeType="";
		try {
			mimeType=new String(data, "UTF-8");
		} catch(UnsupportedEncodingException e) {
			ohMyGodUtf8IsNotSupportedWhatShouldIDo(e);
			return null;
		}


		DataFlavor flavor=null;
		try {
			flavor = new DataFlavor(mimeType);
		} catch(ClassNotFoundException e) {
			//MAYBE silent
			e.printStackTrace();
		}
		return flavor;
	}

	public static byte[] serializeData(Transferable data, DataFlavor flavor) throws IOException, UnsupportedFlavorException {
		if(!flavor.isRepresentationClassInputStream())
			throw new UnsupportedEncodingException("Must be an InputStream Representative");
		InputStream is = (InputStream)data.getTransferData(flavor);
		return IOUtils.toByteArray(is);
	}

	public static InputStream deserializeData(byte[] data){
		return new ByteArrayInputStream(data);
	}


	static void ohMyGodUtf8IsNotSupportedWhatShouldIDo(UnsupportedEncodingException e) throws Fit{
		e.printStackTrace();
		throw new Fit("UTF8 is not supported on this System, I can't live on.", e);
	}


	static String join(Iterable<String> strings, String glue){
		StringBuilder sb = new StringBuilder();
		boolean first=true;
		for(String string : strings) {
			if(!first) {
				sb.append(glue);
			} else {
				first=false;
			}
			sb.append(string);
		}
		return sb.toString();
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
		super("This Program just threw a Fit: "+message, cause);
	}
}


/**
 * Just a simple broadcast barrier, where a signal releases all waiters, waits for them to be started and resumes its work.
 *
 */
class SimpleBarrier{
	private ReadWriteLock rwl;

	SimpleBarrier(){
		rwl = new ReentrantReadWriteLock(true);
		//Set the write-lock by default so all waiting processes will wait for it to be released.
		rwl.writeLock().lock();
	}

	/**
	 * The current thread sleeps till the barrier receives a signal call.
	 */
	void waitForSignal(){
		//Wait for the WriteLock to be released and then allow the write-lock to lock the state again.
		rwl.readLock().lock();
		rwl.readLock().unlock();
	}

	/**
	 * releases all sleeping threads which wait for a signal on this barrier, resets the barrier and returns.
	 */
	void signal(){
		//allow all read-locks to be locked and then unlocked to release all waiting processes.
		rwl.writeLock().unlock();
		//then begin accumulating all read-locks again.
		rwl.writeLock().lock();
	}
}
