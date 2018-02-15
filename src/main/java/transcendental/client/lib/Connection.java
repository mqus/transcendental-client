package transcendental.client.lib;

public interface Connection {
	void bind(ClipboardAdaptor cl);

	Connection setStateChangeListener(StateChangeListener listener);

	Exception getLastException();

	/**
	 * sends the data to the server; retries as long the send fails
	 *
	 * @param pkg the serialized package (Packager.pack())
	 * @param beStubborn if this parameter is true, the connection will try to send the packet,
	 *                      even if the connection is completely shut down.
	 */
	void reliableSend(Packager.SerializablePackage pkg, boolean beStubborn);

	/**
	 * sends the data to the server.
	 *
	 * @param pkg the serialized package (Packager.pack())
	 * @return true if the send was successful, false if the Connection was interrupted or a timeout was reached.
	 */
	boolean send(Packager.SerializablePackage pkg);

	/**
	 * Waits for an incoming package, deserializes it and returns it.
	 *
	 * @return the received Package <b>or</b><br>
	 * {@code null} , if the connection was interrupted or not there.<br>
	 * {@code Package.BAD_PACKAGE}, if the package could not be decoded correctly.
	 */
	Package recv();

	boolean open();

	Exception tryConnect();

	void disconnect();

	ConnState getState();

	void waitForConnection();
}
