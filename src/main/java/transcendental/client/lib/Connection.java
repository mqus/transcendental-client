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
public class Connection {
	private String server = "localhost";
	private int port = 19192;
	private int connectTimeout = 5;
	private int maxRetryInterval = 30;
	private StateChangeListener listener = StateChangeListener.SILENT;

	private Socket conn;
	private ConnState s = ConnState.NO_CONNECTION;
	private Exception lastException = null;
	private Client client = null;
	private Reader r = null;
	private SimpleBarrier broadcast;


	public Connection() {
		broadcast = new SimpleBarrier();
	}

	public Connection(String server, int port) {
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

	public void bind(Client cl) {
		this.client = cl;
	}

	public Connection setStateChangeListener(StateChangeListener listener) {
		if(listener == null)
			this.listener = StateChangeListener.SILENT;
		else
			this.listener = listener;
		return this;
	}

	public Exception getLastException() {
		return this.lastException;
	}

	public void reliableSend(byte[] pkg, boolean beStubborn) {
		//as long as send fails, try again!
		while(send(pkg) == false)
			//If there is no connection and currently no attempt to reconnect, abort the send anyway
			if(!beStubborn && s == ConnState.NO_CONNECTION) ;
		return;
	}


	/**
	 * sends the data to the server.
	 *
	 * @param data the serialized package data (Packager.pack())
	 * @return true if the send was successful, false if the Connection was interrupted or a timeout was reached.
	 */
	public boolean send(byte[] data) {
		if(s != ConnState.CONNECTED)
			return false;
		try {
			conn.setTcpNoDelay(true);
			conn.getOutputStream().write(data);
		} catch(IOException e) {
			exceptionOccured(e);
			return false;
		}
		return true;
	}

	/**
	 * Waits for an incoming package, deserializes it and returns it.
	 *
	 * @return the received Package <b>or</b><br>
	 * {@code null} , if the connection was interrupted or not there.<br>
	 * {@code Package.BAD_PACKAGE}, if the package could not be decoded correctly.
	 */
	public Package recv() {
		if(s != ConnState.CONNECTED)
			return null;

		try {
			return this.client.getPackager().deserialize(r);
		} catch(JsonSyntaxException e) {
			return Package.BAD_PACKAGE;
		} catch(JsonIOException e) {
			exceptionOccured(e);
			return null;
		}
	}

	public boolean open() {
		if(this.client == null) {
			throw new Error("Didn't bind client to conn before attempting co open a connection.");
		}
		changeState(ConnState.CONNECTING);

		try {
			conn = new Socket(server, port);
			conn.getOutputStream().write(this.client.getPackager().packHello());
			r = new InputStreamReader(conn.getInputStream(), "UTF-8");
		} catch(IOException e) {
			exceptionOccured(e);
			return false;
		}

		changeState(ConnState.CONNECTED);
		return true;
	}

	public Exception tryConnect() {
		try {
			Socket c = new Socket(server, port);
			c.getOutputStream().write(this.client.getPackager().packHello());
		} catch(IOException e) {
			return e;
		}
		return null;
	}

	public void disconnect() {
		changeState(ConnState.NO_CONNECTION);
		try {
			if(!conn.isClosed())
				conn.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	private void retryConnect() {
		int t = 1;
		while(s != ConnState.CONNECTED && s != ConnState.NO_CONNECTION) {
			try {
				Thread.sleep(t * 1000);
			} catch(InterruptedException ignored) {

			}

			changeState(ConnState.CONNECTING);

			try {
				conn = new Socket(server, port);
				conn.getOutputStream().write(this.client.getPackager().packHello());
				changeState(ConnState.CONNECTED);
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
		if(s == ConnState.NO_CONNECTION) return;

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

	public ConnState getState() {
		return s;
	}

	public void waitForConnection() {
		broadcast.waitForSignal();
	}

}
