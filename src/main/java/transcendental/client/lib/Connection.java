package transcendental.client.lib;

import java.io.IOException;

public abstract class Connection {
	protected int maxRetryInterval = 30;
	protected Packager packager = null;
	protected SimpleBarrier broadcast;
	private StateChangeListener listener = StateChangeListener.SILENT;
	protected ConnState s = ConnState.NO_CONNECTION;
	private Exception lastException = null;

	protected Connection() {
		broadcast = new SimpleBarrier();
	}

	/**
	 * Sets the maximum amount of seconds to wait between reconnect tries
	 * @param maxRetryInterval The maximum amount of seconds to wait between retries. If 0 or negative, don't try to reconnect.
	 * @return {@code this} {@code Connection}, to chain calls.
	 */
	public Connection setMaxRetryInterval(int maxRetryInterval) {
		this.maxRetryInterval = maxRetryInterval;
		return this;
	}

	/**
	 * Sets the callback interface, on which every state change of this connection is notified.
	 * This Callback is blocking!
	 *
	 * @param listener The listening interface. this connection will only call the {@code handleConnStateChange} method.
	 * @return {@code this} {@code Connection}, to chain calls.
	 */
	public Connection setStateChangeListener(StateChangeListener listener) {
		if(listener == null)
			this.listener = StateChangeListener.SILENT;
		else
			this.listener = listener;
		return this;
	}

	/**
	 * Bind the connection to a Packager which provides (de-)serialisation functionalities.
	 *
	 * Note: should only be called from within this library
	 *
	 * @param p the packager this connection should use
	 */
	public void bind(Packager p) {
		this.packager = p;
	}


	/**
	 * @return the last thrown {@code Exception}, which triggered an EXCEPTION state.
	 */
	public Exception getLastException() {
		return this.lastException;
	}

	/**
	 * Sends the data to the appropriate receiver(s); retries as long the send fails
	 *
	 * Note: should only be called from within this library
	 * @param pkg the serialized package (Packager.pack())
	 * @param beStubborn if this parameter is true, the connection will try to send the packet,
	 *                      even if the connection is completely shut down.
	 */
	public void reliableSend(Packager.SerializablePackage pkg, boolean beStubborn) {
		//as long as send fails, try again!
		while (!send(pkg))
			//If there is no connection and currently no attempt to reconnect, abort the send anyway
			if (!beStubborn && (s == ConnState.NO_CONNECTION || s == ConnState.DISCONNECTING))
				return;
	}

	/**
	 * Sends the data to the appropriate receiver(s).
	 *
	 * Note: should only be called from within this library
	 * @param pkg the serialized package (Packager.pack())
	 * @return true if the send was successful, false if the Connection was interrupted or a timeout was reached.
	 */
	public abstract boolean send(Packager.SerializablePackage pkg);

	/**
	 * Waits for an incoming package, deserializes it and returns it.
	 *
	 * Note: should only be called from within this library
	 * @return the received Package <b>or</b><br>
	 * {@code null} , if the connection was interrupted or not there.<br>
	 * {@code Package.BAD_PACKAGE}, if the package could not be decoded correctly.
	 */
	public abstract Package recv();

	/**
	 * Connects to the other clients.
	 *
	 * Note: should only be called from within this library
	 * @return {@code true}, if a connection was successfully opened.
	 */
	public abstract boolean open();

	/**
	 * Test the connection
	 *
	 */
	public abstract void tryConnect() throws IOException;

	/**
	 * Disconnects from the other clients. After this, the connection could be opened again with open().
	 *
	 * Note: should only be called from within this library
	 * @see ClipboardAdaptor#disconnect()
	 */
	public abstract void disconnect();

	protected abstract void retryConnect();

	protected void exceptionOccured(Exception e) {
		exceptionOccured(e, false);
	}

	protected void exceptionOccured(Exception e, boolean onlyTriggerStateChange) {
		if(s == ConnState.NO_CONNECTION || s == ConnState.DISCONNECTING) return;

		this.lastException = e;
		changeState(ConnState.EXCEPTION);

		if(onlyTriggerStateChange) return;

		if(this.maxRetryInterval <= 0) {
			changeState(ConnState.NO_CONNECTION);
		} else {
			changeState(ConnState.FAILED);
			new Thread(new Runnable() {
				@Override
				public void run() {
					retryConnect();
				}
			}).start();
		}
	}

	protected void changeState(ConnState newState) {
		changeState(newState, false);
	}

	protected void changeState(ConnState newState, boolean silent) {
		if(this.s == newState)
			return;

		if(!silent)
			listener.handleConnStateChange(newState);

		if(newState == ConnState.CONNECTED)
			broadcast.signal();
		this.s = newState;
	}

	/**
	 * @return the current state of the Connection.
	 */
	public ConnState getState() {
		return s;
	}

	/**
	 * wait till the connection state is CONNECTED, meaning a connection is established.
	 *
	 * Note: should only be called from within this library
	 */
	public abstract void waitForConnection();
}
