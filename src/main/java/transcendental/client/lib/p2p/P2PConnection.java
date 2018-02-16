package transcendental.client.lib.p2p;

import transcendental.client.lib.ClipboardAdaptor;
import transcendental.client.lib.Connection;
import transcendental.client.lib.Package;
import transcendental.client.lib.Packager;

import java.io.IOException;

public class P2PConnection extends Connection{

	/**
	 * Sends the data to the appropriate receiver(s).
	 * <p>
	 * Note: should only be called from within this library
	 *
	 * @param pkg the serialized package (Packager.pack())
	 * @return true if the send was successful, false if the Connection was interrupted or a timeout was reached.
	 */
	@Override
	public boolean send(Packager.SerializablePackage pkg) {
		//TODO
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
		//TODO
		return null;
	}

	/**
	 * Connects to the other clients.
	 * <p>
	 * Note: should only be called from within this library
	 *
	 * @return {@code true}, if a connection was successfully opened.
	 */
	@Override
	public boolean open() {
		//TODO
		return false;
	}

	/**
	 * Test the connection
	 */
	@Override
	public void tryConnect() throws IOException {
		//TODO

	}

	/**
	 * Disconnects from the other clients. After this, the connection could be opened again with open().
	 * <p>
	 * Note: should only be called from within this library
	 *
	 * @see ClipboardAdaptor#disconnect()
	 */
	@Override
	public void disconnect() {
		//TODO

	}

	@Override
	protected void retryConnect() {
		//TODO

	}

	/**
	 * wait till the connection state is CONNECTED, meaning a connection is established.
	 * <p>
	 * Note: should only be called from within this library
	 */
	@Override
	public void waitForConnection() {
		//TODO

	}
}
