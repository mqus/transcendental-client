package transcendental.test;

import org.fourthline.cling.UpnpServiceImpl;
import org.fourthline.cling.binding.LocalServiceBindingException;
import org.fourthline.cling.binding.annotations.*;
import org.fourthline.cling.model.DefaultServiceManager;
import org.fourthline.cling.model.ValidationException;
import org.fourthline.cling.model.meta.*;
import org.fourthline.cling.model.types.DeviceType;
import org.fourthline.cling.model.types.UDADeviceType;
import org.fourthline.cling.model.types.UDN;

import java.io.IOException;


@UpnpService(
		serviceId = @UpnpServiceId("transcendental"),
		serviceType = @UpnpServiceType(value = "transcendental")
)
public class SSDPTestService implements Runnable{
	@UpnpStateVariable(defaultValue = "0", sendEvents = false)
	private boolean target = false;

	@UpnpStateVariable(defaultValue = "0")
	private boolean status = false;

	@UpnpAction
	public void setTarget(@UpnpInputArgument(name = "NewTargetValue")
								  boolean newTargetValue) {
		target = newTargetValue;
		status = newTargetValue;
		System.out.println("Switch is: " + status);
	}

	@UpnpAction(out = @UpnpOutputArgument(name = "RetTargetValue"))
	public boolean getTarget() {
		return target;
	}

	@UpnpAction(out = @UpnpOutputArgument(name = "ResultStatus"))
	public boolean getStatus() {
		// If you want to pass extra UPnP information on error:
		// throw new ActionException(ErrorCode.ACTION_NOT_AUTHORIZED);
		return status;
	}

	LocalDevice createDevice()
			throws ValidationException, LocalServiceBindingException, IOException {

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
				new AnnotationLocalServiceBinder().read(SSDPTestService.class);

		switchPowerService.setManager(
				new DefaultServiceManager(switchPowerService, SSDPTestService.class)
		);

		return new LocalDevice(identity, type, details, /*icon,*/ switchPowerService);

    /* Several services can be bound to the same device:
    return new LocalDevice(
            identity, type, details, icon,
            new LocalService[] {switchPowerService, myOtherService}
    );
    */

	}

	public static void main(String[] args) throws Exception {
		// Start a user thread that runs the UPnP stack
		Thread serverThread = new Thread(new SSDPTestService());
		serverThread.setDaemon(false);
		serverThread.start();
		System.out.println("FERTIG!-2");

	}

	public void run() {
		try {

			final org.fourthline.cling.UpnpService upnpService = new UpnpServiceImpl();

			Runtime.getRuntime().addShutdownHook(new Thread() {
				@Override
				public void run() {
					System.out.println("SHUT UP (DOWN)");
					upnpService.shutdown();
					System.out.println("SHUT UP (DOWN)4");
				}
			});

			// Add the bound local device to the registry
			upnpService.getRegistry().addDevice(
					createDevice()
			);

			System.out.println("FERTIG!-1");
		} catch (Exception ex) {
			System.err.println("Exception occured: " + ex);
			ex.printStackTrace(System.err);
			System.exit(1);
		}
		System.out.println("FERTIG!");
	}

}
