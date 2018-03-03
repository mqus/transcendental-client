package transcendental.test;

import transcendental.client.lib.Util;

import java.awt.*;
import java.awt.datatransfer.*;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.util.Arrays;

/**
 * Created by markus on 26.01.17.
 */
public class Main {


	public static void main(String[] args) {
		System.out.println("_____");
		System.out.println(System.getProperty("os.name"));
		System.out.println("_____");
		final Clipboard x = Toolkit.getDefaultToolkit().getSystemClipboard();
		x.addFlavorListener(new FlavorListener() {
			@Override
			public void flavorsChanged(FlavorEvent e) {
				System.err.println("----------------------");
				for(DataFlavor dataFlavor : x.getAvailableDataFlavors()) {
					if(dataFlavor.isRepresentationClassInputStream()) {
						System.out.println(dataFlavor.getMimeType());
						if(dataFlavor.isMimeTypeEqual("text/uri-list; class=java.io.InputStream")) {
							try {
								InputStream data = (InputStream) x.getData(dataFlavor);
								byte[] buffer = new byte[1024*1024];
								int read = data.read(buffer);
								if(read==1024*1024)
									System.err.println("BIG");
								data.close();
								System.out.println("\t"+new String(Arrays.copyOf(buffer,read)));
							} catch(UnsupportedFlavorException e1) {
								e1.printStackTrace();
							} catch(IOException e1) {
								e1.printStackTrace();
							}
						}
					}
				}
			}
		});
		while(true) {
			try {
				Thread.sleep(10000L);
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
		}
		//byte[] p = Package.createHelloPackage("Raum1".getBytes());
		//System.out.println(p);
		//System.out.println(new String(p,"UTF-8"));
		//System.out.println(new String("Raum1".getBytes(), "UTF-8"));

	}


	public static void main2(String[] args) throws InvalidKeyException {
//		Clipboard x = Toolkit.getDefaultToolkit().getSystemClipboard();
//		//System.out.println(Arrays.deepToString(x.getAvailableDataFlavors()));
//		System.out.println(x.getName());
//		System.out.println(System.getProperty("awt.toolkit"));
//		aToString(x.getAvailableDataFlavors());
//		System.out.println("--");
//		System.out.println(DataFlavor.imageFlavor.toString());
//		System.out.println(new DataFlavor("text/html"));

//		Socket clientSocket;
//		try {
//			Packager pr = new Packager("RaumRaumRaumRaum1");
//			clientSocket = new Socket("localhost", 19192);
//			byte[] p = pr.packHello();
//			clientSocket.setTcpNoDelay(true);
//			clientSocket.getOutputStream().write(p);
//			int n = clientSocket.getInputStream().read(p);
//			System.out.println("read:" + n);
//		} catch(IOException e) {
//			e.printStackTrace();
//
//		}

	}

	public static void main4(String[] args) throws InterruptedException {
		final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		final DataFlavor[] flavors = clipboard.getAvailableDataFlavors();
		clipboard.addFlavorListener(new FlavorListener() {
			@Override
			public void flavorsChanged(FlavorEvent e) {

				for (DataFlavor flavor : clipboard.getAvailableDataFlavors()) {
//					if(!flavor.isRepresentationClassInputStream())continue;
					System.out.println("   ");
					System.out.println(flavor);
					System.out.println(flavor.getHumanPresentableName());
					System.out.println(flavor.getMimeType());
					System.out.print("\t" + (flavor.isMimeTypeSerializedObject() ? "y" : "n"));
					System.out.print("\t" + (flavor.isFlavorSerializedObjectType() ? "y" : "n"));
					System.out.print("\t" + (flavor.isRepresentationClassByteBuffer() ? "y" : "n"));
					System.out.print("\t" + (flavor.isRepresentationClassInputStream() ? "y" : "n"));
					System.out.print("\t" + (flavor.isRepresentationClassReader() ? "y" : "n"));
					System.out.print("\t" + (flavor.isRepresentationClassRemote() ? "y" : "n"));
					System.out.println("\t" + (flavor.isRepresentationClassSerializable() ? "y" : "n"));
				}
				System.err.println("---");
			}
		});

		Thread.sleep(1000 * 3600);

	}

}
