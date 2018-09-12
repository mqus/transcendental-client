package transcendental.client.lib.p2p;

import org.fourthline.cling.binding.annotations.*;

@UpnpService(
		serviceId = @UpnpServiceId("transcendental"),
		serviceType = @UpnpServiceType(value = "transcendental")
)
public class SSDPDevice {

	@UpnpStateVariable
	private int port;

	@UpnpAction
	public void setPort(@UpnpInputArgument(name = "ListenerPort") int port) {
		this.port=port;
		System.out.println("Port set to: " + port);
	}

	@UpnpAction(out = @UpnpOutputArgument(name = "ListenerPort"))
	public int getPort() {
		System.out.println("Port requested: " + port);
		return port;
		// If you want to pass extra UPnP information on error:
		// throw new ActionException(ErrorCode.ACTION_NOT_AUTHORIZED);
	}
}
