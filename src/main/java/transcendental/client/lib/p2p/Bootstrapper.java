package transcendental.client.lib.p2p;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.IllegalFormatException;
import java.util.Vector;

public class Bootstrapper {

	private Vector<SocketAddress> bootstrapPeers;

	public Bootstrapper(SocketAddress... bootstrapPeers) {
		this.bootstrapPeers = new Vector<>(Arrays.asList(bootstrapPeers));
	}

	public Bootstrapper(String... bootstrapPeers) throws Exception {
		this.bootstrapPeers = new Vector<>();
		for (String bootstrapPeer : bootstrapPeers) {
			this.bootstrapPeers.add(toSocketAddress(bootstrapPeer));
		}
	}

	public Vector<SocketAddress> getBootstrapPeers() {
		return new Vector<>(bootstrapPeers);
	}

	public void addBootstrapPeer(String peer) throws Exception {
		this.addBootstrapPeer(toSocketAddress(peer));
	}

	protected void addBootstrapPeer(SocketAddress socketAddress) {
		this.bootstrapPeers.add(socketAddress);
	}


	static SocketAddress toSocketAddress(String in) throws Exception {
		URI u= null;
		try {
			u = new URI("my://"+in);
		} catch (URISyntaxException e) {
			throw new Exception("Error: '" + in + "is not a valid adress",e);
		}
		String host=u.getHost();
		if(host==null)
			host="0.0.0.0";
		int port=u.getPort();
		if(port==-1){
			if(u.getHost()==null){
				String[] parts = in.split(":");
				port=Integer.parseInt(parts[parts.length-1]);
			}

		}
		if(port==-1)
			port=P2PConnection.DEFAULT_PORT;


		if(host.equals("0.0.0.0"))
			return new InetSocketAddress(port);
		else
			return new InetSocketAddress(host,port);
	}
}
