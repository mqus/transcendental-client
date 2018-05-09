package transcendental.client.lib.p2p;

import java.net.SocketAddress;

public class SSDPBootstrapper extends Bootstrapper {
	public SSDPBootstrapper(SocketAddress... bootstrapPeers) {
		super(bootstrapPeers);
	}

	public SSDPBootstrapper(String... bootstrapPeers) throws Exception {
		super(bootstrapPeers);
	}
}
