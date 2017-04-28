package transcendental.test;

import java.awt.*;
import java.awt.datatransfer.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.security.InvalidKeyException;

import com.google.gson.GsonBuilder;
import transcendental.client.lib.Package;

import com.google.gson.Gson;

import org.apache.commons.codec.binary.Base64;
import transcendental.client.lib.Packager;
import transcendental.client.lib.Util;

/**
 * Created by markus on 26.01.17.
 */
public class Main {


	public static void main1(String[] args) throws UnsupportedEncodingException {
		//byte[] p = Package.createHelloPackage("Raum1".getBytes());
		//System.out.println(p);
		//System.out.println(new String(p,"UTF-8"));
		//System.out.println(new String("Raum1".getBytes(), "UTF-8"));

	}



	public static void main2(String[] args) throws ClassNotFoundException, InvalidKeyException {
//		Clipboard x = Toolkit.getDefaultToolkit().getSystemClipboard();
//		//System.out.println(Arrays.deepToString(x.getAvailableDataFlavors()));
//		System.out.println(x.getName());
//		System.out.println(System.getProperty("awt.toolkit"));
//		aToString(x.getAvailableDataFlavors());
//		System.out.println("--");
//		System.out.println(DataFlavor.imageFlavor.toString());
//		System.out.println(new DataFlavor("text/html"));

		Socket clientSocket;
		try {
			Packager pr=new Packager("RaumRaumRaumRaum1");
			clientSocket = new Socket("localhost", 19192);
			byte[] p = pr.packHello();
			clientSocket.setTcpNoDelay(true);
			clientSocket.getOutputStream().write(p);
			int n=clientSocket.getInputStream().read(p);
			System.out.println("read:"+n);
		} catch (IOException e) {
			e.printStackTrace();

		}

	}

	public static void main5(String[] args) throws InterruptedException {
		final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		final DataFlavor[] flavors = clipboard.getAvailableDataFlavors();
		clipboard.addFlavorListener(new FlavorListener() {
			@Override
			public void flavorsChanged(FlavorEvent e) {

				for(DataFlavor flavor : clipboard.getAvailableDataFlavors()) {
					if(!flavor.isRepresentationClassInputStream())continue;
					System.out.println("   ");
					System.out.println(flavor);
					System.out.println(flavor.getHumanPresentableName());
					System.out.println(flavor.getMimeType());
					System.out.print("\t"+(flavor.isMimeTypeSerializedObject()?"y":"n"));
					System.out.print("\t"+(flavor.isFlavorSerializedObjectType()?"y":"n"));
					System.out.print("\t"+(flavor.isRepresentationClassByteBuffer()?"y":"n"));
					System.out.print("\t"+(flavor.isRepresentationClassInputStream()?"y":"n"));
					System.out.print("\t"+(flavor.isRepresentationClassReader()?"y":"n"));
					System.out.print("\t"+(flavor.isRepresentationClassRemote()?"y":"n"));
					System.out.println("\t"+(flavor.isRepresentationClassSerializable()?"y":"n"));
				}
				System.err.println("---");
			}
		});

		Thread.sleep(1000*3600);

	}

	private static void aToString(Object[] args){
		for (Object arg : args) {
			System.out.println(arg.toString());
		}
	}
}

class Dings implements ClipboardOwner,Transferable{
	/**
	 * Notifies this object that it is no longer the clipboard owner.
	 * This method will be called when another application or another
	 * object within this application asserts ownership of the clipboard.
	 *
	 * @param clipboard the clipboard that is no longer owned
	 * @param contents  the contents which this owner had placed on the clipboard
	 */
	public void lostOwnership(Clipboard clipboard, Transferable contents) {

	}

	/**
	 * Returns an array of DataFlavor objects indicating the flavors the data
	 * can be provided in.  The array should be ordered according to preference
	 * for providing the data (from most richly descriptive to least descriptive).
	 *
	 * @return an array of data flavors in which this data can be transferred
	 */
	public DataFlavor[] getTransferDataFlavors() {
		return new DataFlavor[0];
	}

	/**
	 * Returns whether or not the specified data flavor is supported for
	 * this object.
	 *
	 * @param flavor the requested flavor for the data
	 * @return boolean indicating whether or not the data flavor is supported
	 */
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return false;
	}

	/**
	 * Returns an object which represents the data to be transferred.  The class
	 * of the object returned is defined by the representation class of the flavor.
	 *
	 * @param flavor the requested flavor for the data
	 * @throws IOException                if the data is no longer available
	 *                                    in the requested flavor.
	 * @throws UnsupportedFlavorException if the requested data flavor is
	 *                                    not supported.
	 * @see DataFlavor#getRepresentationClass
	 */
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		return null;
	}
}
