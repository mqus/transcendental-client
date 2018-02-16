package transcendental.client.lib;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.Socket;

/**
 * Created by markus on 29.01.17.
 */
public class ServerConnection extends Connection {
	private String server = "localhost";
	private int port = 19192;
	private int connectTimeout = 5;

	private Socket conn;
	private Reader r = null;


	public ServerConnection() {
		super();
	}

	public ServerConnection(String server, int port) {
		this();
		this.server = server;
		this.port = port;
	}

	public ServerConnection setServer(String server) {
		this.server = server;
		return this;
	}

	public ServerConnection setPort(int port) {
		this.port = port;
		return this;
	}

	public ServerConnection setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
		return this;
	}


	/**
	 * Sends the data to the server.
	 *
	 * Note: should only be called from within this library
	 * @param pkg the serialized package (Packager.pack())
	 * @return true if the send was successful, false if the Connection was interrupted or a timeout was reached.
	 */
	@Override
	public boolean send(Packager.SerializablePackage pkg) {
		if(s != ConnState.CONNECTED)
			return false;
		try {
			conn.setTcpNoDelay(true);
			conn.getOutputStream().write(pkg.serialize());
		} catch(IOException e) {
			exceptionOccured(e);
			return false;
		}
		return true;
	}

	/**
	 * Waits for an incoming package, deserializes it and returns it.
	 *
	 * Note: should only be called from within this library
	 * @return the received Package <b>or</b><br>
	 * {@code null} , if the connection was interrupted or not there.<br>
	 * {@code Package.BAD_PACKAGE}, if the package could not be decoded correctly.
	 */
	@Override
	public Package recv() {
		if(s != ConnState.CONNECTED)
			return null;

		try {
			Package pkg = packager.deserialize(r);
			if (pkg == null) {
				//connection was lost, handle it:
				exceptionOccured(new ConnectionLostException());
			}
			return pkg;
		} catch(JsonSyntaxException e) {
			return Package.BAD_PACKAGE;
		} catch(JsonIOException e) {
			exceptionOccured(e);
			return null;
		}
	}

	/**
	 * Connects to the server.
	 *
	 * Note: should only be called from within this library
	 * @return {@code true}, if a connection was successfully opened.
	 */
	@Override
	public boolean open() {
		if(this.packager == null) {
			throw new Error("Didn't bind conn to a packager before attempting co open a connection.");
		}
		try {
			connect();
		} catch(IOException e) {
			exceptionOccured(e);
			return false;
		}
		return true;
	}

	private void connect() throws IOException {
		changeState(ConnState.CONNECTING);
		conn = new Socket(server, port);
		conn.getOutputStream().write(packager.packHello());
		r = new InputStreamReader(conn.getInputStream(), "UTF-8");
		changeState(ConnState.CONNECTED);
	}

	/**
	 * Test the connection
	 *
	 */
	@Override
	public void tryConnect() throws IOException{

		Socket c = new Socket(server, port);
		c.getOutputStream().write(packager.packHello());
		c.close();
	}

	/**
	 * Disconnect from the server. After this, the connection could be opened again with open().
	 *
	 * Note: should only be called from within this library
	 * @see ClipboardAdaptor#disconnect()
	 */
	@Override
	public void disconnect() {
		changeState(ConnState.DISCONNECTING);
		try {
			if(!conn.isClosed())
				conn.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
		changeState(ConnState.NO_CONNECTION);
	}

	@Override
	protected void retryConnect() {
		int t = 1;
		while(s != ConnState.CONNECTED && s != ConnState.NO_CONNECTION) {
			try {
				Thread.sleep(t * 1000);
			} catch(InterruptedException ignored) {

			}

			try {
				connect();
			} catch(IOException e) {
				exceptionOccured(e, true);
				changeState(ConnState.FAILED);
			}
			t = Math.min(2 * t, maxRetryInterval);
		}
	}

	/**
	 * Waits until the connection state is CONNECTED, meaning a connection is established.
	 *
	 * Note: should only be called from within this library
	 */
	@Override
	public void waitForConnection() {
		broadcast.waitForSignal();
	}

}
