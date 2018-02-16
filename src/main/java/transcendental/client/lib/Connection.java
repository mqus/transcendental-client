package transcendental.client.lib;

/**
 * Connection is the general abstraction over the functionality the connection layer should provide for clipboard sharing.
 */
public interface Connection {
	/**
	 * Bind the connection to a Packager which provides (de-)serialisation functionalities.
	 *
	 * Note: should only be called from within this library
	 * @param p the packager this connection should use
	 */
	void bind(Packager p);

	/**
	 * Sets the callback interface, on which every state change of this connection is notified.
	 * This Callback is blocking!
	 *
	 * @param listener The listening interface. this connection will only call the {@code handleConnStateChange} method.
	 * @return {@code this} {@code Connection}, to chain calls.
	 */
	Connection setStateChangeListener(StateChangeListener listener);

	/**
	 * @return the last thrown {@code Exception}, which triggered an EXCEPTION state.
	 */
	Exception getLastException();

	/**
	 * Sends the data to the appropriate receiver; retries as long the send fails
	 *
	 * Note: should only be called from within this library
	 * @param pkg the serialized package (Packager.pack())
	 * @param beStubborn if this parameter is true, the connection will try to send the packet,
	 *                      even if the connection is completely shut down.
	 */
	void reliableSend(Packager.SerializablePackage pkg, boolean beStubborn);

	/**
	 * Sends the data to the appropriate receiver.
	 *
	 * Note: should only be called from within this library
	 * @param pkg the serialized package (Packager.pack())
	 * @return {@code true} if the send was successful, false if the Connection was interrupted or a timeout was reached.
	 */
	boolean send(Packager.SerializablePackage pkg);

	/**
	 * Waits for an incoming package, deserializes it and returns it.
	 *
	 * Note: should only be called from within this library
	 * @return the received Package <b>or</b><br>
	 * {@code null} , if the connection was interrupted or not there.<br>
	 * {@code Package.BAD_PACKAGE}, if the package could not be decoded correctly.
	 */
	Package recv();

	/**
	 * Connects to the other clients.
	 *
	 * Note: should only be called from within this library
	 * @return {@code true}, if a connection was successfully opened.
	 * @see ClipboardAdaptor#connectAndRun()
	 */
	boolean open();

	/**
	 * Test the connection
	 *
	 */
	void tryConnect()throws Exception;

	/**
	 * Disconnect from the other clients. After this, the connection could be opened again with open().
	 * Note: should only be called from within this library
	 * @see ClipboardAdaptor#disconnect()
	 */
	void disconnect();

	/**
	 * @return the current state of the Connection.
	 */
	ConnState getState();

	/**
	 * wait till the connection state is CONNECTED, meaning a connection is established.
	 *
	 * Note: should only be called from within this library
	 */
	void waitForConnection();
}
