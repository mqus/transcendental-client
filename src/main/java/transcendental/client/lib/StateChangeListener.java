package transcendental.client.lib;

/**
 * Created by markus on 26.01.17.
 */
public interface StateChangeListener {
	StateChangeListener SILENT = new DefaultListener(false);
	StateChangeListener VERBOSE = new DefaultListener(true);

	/**
	 * handleConnStateChange is called directly after the Connection State changes.
	 * the Connection waits for that callback to do anything and continues after
	 * its completion.
	 * @param newState the State the Connection changes to.
	 */
	void handleConnStateChange(ConnState newState);

	/**
	 * handleClientStateChange is called directly after the Client State changes.
	 * the Client waits for that callback to do anything and continues after
	 * its completion.
	 * @param newState the State the Client changes to.
	 */
	void handleClientStateChange(ClientState newState);
}
class DefaultListener implements StateChangeListener {
	private boolean verbose;

	DefaultListener(boolean verbose) {
		this.verbose = verbose;
	}

	@Override
	public void handleConnStateChange(ConnState newState) {
		if(verbose) System.out.println("New Connection State: "+newState);
		//Do nothing else
	}

	@Override
	public void handleClientStateChange(ClientState newState) {
		if(verbose) System.out.println("New Client State: "+newState);
		//Do nothing else
	}
}

