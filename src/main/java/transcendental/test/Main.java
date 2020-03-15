package transcendental.test;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.UpnpServiceImpl;
import org.fourthline.cling.model.ValidationException;
import org.fourthline.cling.model.message.header.STAllHeader;
import org.fourthline.cling.model.meta.*;
import org.fourthline.cling.registry.Registry;
import org.fourthline.cling.registry.RegistryListener;
import transcendental.client.lib.p2p.SSDPDiscoveryService;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.FlavorEvent;
import java.awt.datatransfer.FlavorListener;
import java.net.InetSocketAddress;
import java.security.InvalidKeyException;
import java.util.Arrays;

/**
 * Created by markus on 26.01.17.
 */
public class Main {


	public static void main3(String[] args) {
		System.out.println("_____");
		System.out.println(System.getProperty("os.name"));
		System.out.println("_____");


		//byte[] p = Package.createHelloPackage("Raum1".getBytes());
		//System.out.println(p);
		//System.out.println(new String(p,"UTF-8"));
		//System.out.println(new String("Raum1".getBytes(), "UTF-8"));

	}

	public static void manin(String[] args) throws ValidationException {
		SSDPDiscoveryService sds=new SSDPDiscoveryService();
		sds.startAdvertising(new InetSocketAddress(7896));
		try {
			Thread.sleep(500);
		} catch (InterruptedException ignored) { }
		sds.search();

	}

	public static void main(String[] args) throws Exception{

		// UPnP discovery is asynchronous, we need a callback
		RegistryListener listener = new RegistryListener() {

			public void remoteDeviceDiscoveryStarted(Registry registry,
													 RemoteDevice device) {
				System.out.println(
						"Discovery started: " + device.getDisplayString()
				);
			}

			public void remoteDeviceDiscoveryFailed(Registry registry,
													RemoteDevice device,
													Exception ex) {
				System.out.println(
						"Discovery failed: " + device.getDisplayString() + " => " + ex
				);
			}

			public void remoteDeviceAdded(Registry registry, RemoteDevice device) {
				System.out.println(
						"Remote device available: " + device.getDisplayString()
				);
			}

			public void remoteDeviceUpdated(Registry registry, RemoteDevice device) {
				System.out.println(
						"Remote device updated: " + device.getDisplayString() + ":" + Arrays.toString(device.getServices())
				);
			}

			public void remoteDeviceRemoved(Registry registry, RemoteDevice device) {
				System.out.println(
						"Remote device removed: " + device.getDisplayString()
				);
			}

			public void localDeviceAdded(Registry registry, LocalDevice device) {
				System.out.println(
						"Local device added: " + device.getDisplayString()
				);
			}

			public void localDeviceRemoved(Registry registry, LocalDevice device) {
				System.out.println(
						"Local device removed: " + device.getDisplayString()
				);
			}

			public void beforeShutdown(Registry registry) {
				System.out.println(
						"Before shutdown, the registry has devices: "
								+ registry.getDevices().size()
				);
			}

			public void afterShutdown() {
				System.out.println("Shutdown of registry complete!");

			}
		};

		// This will create necessary network resources for UPnP right away
		System.out.println("Starting Cling...");
		UpnpService upnpService = new UpnpServiceImpl(listener);


		// Send a search message to all devices and services, they should respond soon
		upnpService.getControlPoint().search(new STAllHeader());

		// Let's wait 10 seconds for them to respond
		System.out.println("Waiting 10 seconds before shutting down...");
		Thread.sleep(10000);

		// Release all resources and advertise BYEBYE to other UPnP devices
		System.out.println("Stopping Cling...");
		for (RemoteDevice d : upnpService.getRegistry().getRemoteDevices()) {
			System.out.println(tos(d.getDetails()));
			System.out.print("  ");
			System.out.println(d.getType().getDisplayString());
			System.out.print("  x");
			System.out.println(d.isFullyHydrated());
			System.out.print("  ");
			System.out.print(tos(d.findServices()));

			System.out.println(Arrays.toString(d.findServiceTypes()));

		}
		upnpService.shutdown();
	}

	public static String tos(DeviceDetails dd){
		String sb = String.valueOf(dd.getFriendlyName() +
				", " +
				dd.getManufacturerDetails().getManufacturer() +
				", " +
				dd.getModelDetails().getModelDescription() +
				", " +
				dd.getUpc());
		return sb;
	}
	public static String tos(Service[] s){
		StringBuilder sb = new StringBuilder();

		for(Service serv:s){
			if(serv instanceof RemoteService){
				sb.append(" ")
						.append(((RemoteService) serv));
				sb.append(" ")
						.append(((RemoteService) serv).getDescriptorURI());
				sb.append(" ")
						.append(((RemoteService) serv).);
			}
			sb.append(" ")
					.append(Arrays.toString(serv.getActions()))
					.append(" # ")
					.append(serv.getServiceId())
					.append("::")
					.append(serv.getServiceType())
					.append(" # ")
					.append(Arrays.toString(serv.getStateVariables()))
					.append("\n  ");
		}
		return sb.toString();
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
