package transcendental.client.lib.p2p;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.UpnpServiceImpl;
import org.fourthline.cling.binding.annotations.AnnotationLocalServiceBinder;
import org.fourthline.cling.controlpoint.ActionCallback;
import org.fourthline.cling.model.DefaultServiceManager;
import org.fourthline.cling.model.ValidationException;
import org.fourthline.cling.model.action.ActionArgumentValue;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.message.header.STAllHeader;
import org.fourthline.cling.model.meta.*;
import org.fourthline.cling.model.types.*;
import transcendental.test.SSDPTestService;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class SSDPDiscoveryService {
	private final UpnpService upnpService;
	private LocalDevice upnpDev;

	public SSDPDiscoveryService() {
		upnpService = new UpnpServiceImpl();
	}


	public void search() {
		// Send a search message to all devices and services, they should respond soon
		upnpService.getControlPoint().search();
		try {
			Thread.sleep(3000);
		} catch (InterruptedException ignored) { }
		// Release all resources and advertise BYEBYE to other UPnP devices
		ServiceId serviceId = new UDAServiceId("transcendental");
		Service transcendentalService;
		upnpService.getRegistry().setDiscoveryOptions();
		for (Device d : upnpService.getRegistry().getDevices()) {
			if ((transcendentalService = d.findService(serviceId)) != null) {

				System.out.println("Service discovered: " + transcendentalService);
				//TODO Liste
				ActionInvocation setPortInvocation = new ActionInvocation(transcendentalService.getAction("GetPort"));
				upnpService.getControlPoint().execute(
						new ActionCallback(setPortInvocation) {

							@Override
							public void success(ActionInvocation invocation) {

								assert invocation.getOutput().length == 1;
								ActionArgumentValue remotePort = invocation.getOutput("ListenerPort");
								System.out.println("--Successfully called action:!"+ (int)remotePort.getValue());
							}

							@Override
							public void failure(ActionInvocation invocation,
												UpnpResponse operation,
												String defaultMsg) {
								System.err.println(defaultMsg);
							}
						}
				);
			}

		}
	}

	public void startAdvertising(InetSocketAddress addr) throws ValidationException {

		DeviceIdentity identity =
				new DeviceIdentity(
						UDN.uniqueSystemIdentifier("SSDPTest")
				);

		DeviceType type =
				new UDADeviceType("TranscendentalClipboardProvider", 1);

		DeviceDetails details =
				new DeviceDetails(
						"A friendly ssdp test",
						new ManufacturerDetails("mqus.pw"),
						new ModelDetails(
								"YourPC",
								"This is a service for testing p2p connections via ssdp/upnp.",
								"v1"
						)
				);

/*		Icon icon =
				new Icon(
						"image/png", 48, 48, 8,
						getClass().getResource("icon.png")
				);
*/
		LocalService<SSDPTestService> switchPowerService =
				new AnnotationLocalServiceBinder().read(SSDPDevice.class);

		switchPowerService.setManager(
				new DefaultServiceManager(switchPowerService, SSDPDevice.class)
		);

		ActionInvocation setPortInvocation = new ActionInvocation(switchPowerService.getAction("SetPort"));
		setPortInvocation.setInput("ListenerPort",addr.getPort());

		// Executes asynchronous in the background
		upnpService.getControlPoint().execute(
				new ActionCallback(setPortInvocation) {

					@Override
					public void success(ActionInvocation invocation) {
						assert invocation.getOutput().length == 0;
						System.out.println("--Successfully called action!");
					}

					@Override
					public void failure(ActionInvocation invocation,
										UpnpResponse operation,
										String defaultMsg) {
						System.err.println(defaultMsg);
					}
				}
		);

		upnpDev=new LocalDevice(identity, type, details, /*icon,*/ switchPowerService);
		upnpService.getRegistry().addDevice(upnpDev);
	}

	public void stopAdvertising(){
		upnpService.getRegistry().removeDevice(upnpDev);
		upnpDev=null;
	}


	public void stop(){
		upnpService.shutdown();
	}
}
