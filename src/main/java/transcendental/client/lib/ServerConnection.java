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
public class ServerConnection implements Connection {
	private String server = "localhost";
	private int port = 19192;
	private int connectTimeout = 5;
	private int maxRetryInterval = 30;
	private StateChangeListener listener = StateChangeListener.SILENT;

	private Socket conn;
	private ConnState s = ConnState.NO_CONNECTION;
	private Exception lastException = null;
	private Packager packager = null;
	private Reader r = null;
	private SimpleBarrier broadcast;


	public ServerConnection() {
		broadcast = new SimpleBarrier();
	}

	public ServerConnection(String server, int port) {
		this();
		this.server = server;
		this.port = port;
	}

	public Connection setServer(String server) {
		this.server = server;
		return this;
	}

	public Connection setPort(int port) {
		this.port = port;
		return this;
	}

	public Connection setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
		return this;
	}

	public Connection setMaxRetryInterval(int maxRetryInterval) {
		this.maxRetryInterval = maxRetryInterval;
		return this;
	}

	/**
	 * Bind the connection to a Packager which provides (de-)serialisation functionalities.
	 *
	 * Note: should only be called from within this library
	 *
	 * @param p the packager this connection should use
	 */
	@Override
	public void bind(Packager p) {
		this.packager = p;
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
		if(listener == null)
			this.listener = StateChangeListener.SILENT;
		else
			this.listener = listener;
		return this;
	}

	/**
	 * @return the last thrown {@code Exception}, which triggered an EXCEPTION state.
	 */
	@Override
	public Exception getLastException() {
		return this.lastException;
	}

	/**
	 * sends the data to the server; retries as long the send fails
	 *
	 * Note: should only be called from within this library
	 * @param pkg the serialized package (Packager.pack())
	 * @param beStubborn if this parameter is true, the connection will try to send the packet,
	 *                      even if the connection is completely shut down.
	 */
	@Override
	public void reliableSend(Packager.SerializablePackage pkg, boolean beStubborn) {
		//as long as send fails, try again!
		while (!send(pkg))
			//If there is no connection and currently no attempt to reconnect, abort the send anyway
			if (!beStubborn && (s == ConnState.NO_CONNECTION || s == ConnState.DISCONNECTING))
				return;
	}


	/**
	 * sends the data to the server.
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
	 * Disconnect from the other clients. After this, the connection could be opened again with open().
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

	private void retryConnect() {
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

	private void exceptionOccured(Exception e) {
		exceptionOccured(e, false);
	}

	private void exceptionOccured(Exception e, boolean onlyTriggerStateChange) {
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
					ServerConnection.this.retryConnect();
				}
			}).start();
		}
	}

	private void changeState(ConnState newState) {
		changeState(newState, false);
	}

	private void changeState(ConnState newState, boolean silent) {
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
	@Override
	public ConnState getState() {
		return s;
	}

	/**
	 * wait till the connection state is CONNECTED, meaning a connection is established.
	 *
	 * Note: should only be called from within this library
	 */
	@Override
	public void waitForConnection() {
		broadcast.waitForSignal();
	}

}
