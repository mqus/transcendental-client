package transcendental.client.lib.p2p;

import transcendental.client.lib.*;
import transcendental.client.lib.Package;

public class P2PConnection implements Connection{
	/**
	 * Bind the connection to a Packager which provides (de-)serialisation functionalities.
	 * <p>
	 * Note: should only be called from within this library
	 *
	 * @param p the packager this connection should use
	 */
	@Override
	public void bind(Packager p) {

	}

	/**
	 * Sets the callback interface, on which every state change of this connection is notified.
	 * This Callback is blocking!
	 *
	 * @param listener The listening interface. this connection will only call the {@code handleConnStateChange} method.
	 * @return {@code this} {@code Connection}, to chain calls.
	 */
	@Override
	public Connection setStateChangeListener(StateChangeListener listener) {
		return null;
	}

	/**
	 * @return the last thrown {@code Exception}, which triggered an EXCEPTION state.
	 */
	@Override
	public Exception getLastException() {
		return null;
	}

	/**
	 * Sends the data to the appropriate receiver; retries as long the send fails
	 * <p>
	 * Note: should only be called from within this library
	 *
	 * @param pkg        the serialized package (Packager.pack())
	 * @param beStubborn if this parameter is true, the connection will try to send the packet,
	 */
	@Override
	public void reliableSend(Packager.SerializablePackage pkg, boolean beStubborn) {

	}

	/**
	 * Sends the data to the appropriate receiver.
	 * <p>
	 * Note: should only be called from within this library
	 *
	 * @param pkg the serialized package (Packager.pack())
	 * @return {@code true} if the send was successful, false if the Connection was interrupted or a timeout was reached.
	 */
	@Override
	public boolean send(Packager.SerializablePackage pkg) {
		return false;
	}

	/**
	 * Waits for an incoming package, deserializes it and returns it.
	 * <p>
	 * Note: should only be called from within this library
	 *
	 * @return the received Package <b>or</b><br>
	 * {@code null} , if the connection was interrupted or not there.<br>
	 * {@code Package.BAD_PACKAGE}, if the package could not be decoded correctly.
	 */
	@Override
	public Package recv() {
		return null;
	}

	/**
	 * Connects to the other clients.
	 * <p>
	 * Note: should only be called from within this library
	 *
	 * @return {@code true}, if a connection was successfully opened.
	 * @see ClipboardAdaptor#connectAndRun()
	 */
	@Override
	public boolean open() {
		return false;
	}

	/**
	 * Test the connection
	 */
	@Override
	public void tryConnect() throws Exception {

	}

	/**
	 * Disconnect from the other clients. After this, the connection could be opened again with open().
	 */
	@Override
	public void disconnect() {

	}

	/**
	 * @return the current state of the Connection.
	 */
	@Override
	public ConnState getState() {
		return null;
	}

	/**
	 * wait till the connection state is CONNECTED, meaning a connection is established.
	 * <p>
	 * Note: should only be called from within this library
	 */
	@Override
	public void waitForConnection() {

	}
}
