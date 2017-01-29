package transcendental.client.lib;

import com.google.gson.JsonIOException;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.Socket;

/**
 * Created by markus on 29.01.17.
 */
public class Connection {
	private String server="localhost";
	private int port=19192;
	private int connectTimeout=5;
	private int maxRetryInterval=30;
	private StateChangeListener listener=StateChangeListener.SILENT;

	private Socket conn;
	private State s=State.NO_CONNECTION;
	private Exception lastException=null;
	private Client client=null;
	private Reader r = null;


	public Connection() {
	}

	public Connection(String server, int port) {
		this.server = server;
		this.port = port;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public void setMaxRetryInterval(int maxRetryInterval) {
		this.maxRetryInterval = maxRetryInterval;
	}

	public void bind(Client cl){
		this.client=cl;
	}

	public Connection setStateChangeListener(StateChangeListener listener){
		if(listener==null)
			this.listener=StateChangeListener.SILENT;
		else
			this.listener=listener;
		return this;
	}

	public Exception getLastException(){
		return this.lastException;
	}
	public boolean send(byte[] pkg){
		if(s!=State.CONNECTED)
			return false;


		//TODO
		return true;
	}

	/** Waits for an incoming package, deserializes it and returns it.
	 *
	 * @return the received Package <b>or</b><br>
	 * 		{@code null} , if the connection was interrupted or not there.<br>
	 * 		{@code Package.BAD_PACKAGE}, if the package could not be decoded correctly.
	 */
	public Package recv(){
		if(s!=State.CONNECTED)
			return null;

		try {
			return this.client.getPackager().deserialize(r);
		} catch (JsonSyntaxException e) {
			return Package.BAD_PACKAGE;
		} catch (JsonIOException e){
			exceptionOccured(e);
			return null;
		}
	}

	public boolean open(){
		if(this.client==null){
			throw new Error("Didn't bind client to conn before attempting co open a connection.");
		}
		changeState(State.CONNECTING);

		try {
			conn = new Socket(server, port);
			conn.getOutputStream().write(this.client.getPackager().packHello());
			r = new InputStreamReader(conn.getInputStream(), "UTF-8");
		} catch (IOException e) {
			exceptionOccured(e);
			return false;
		}
		//TODO? notify x that connection is open
		changeState(State.CONNECTED);
		return true;
	}

	public Exception tryConnect(){
		try {
			Socket c = new Socket(server, port);
			c.getOutputStream().write(this.client.getPackager().packHello());
		} catch (IOException e) {
			return e;
		}
		return null;
	}

	public void disconnect(){
		this.changeState(State.NO_CONNECTION);
		try {
			conn.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void retryConnect(){
		int t=1;
		while(s!=State.CONNECTED && s!=State.NO_CONNECTION){
			try {
				Thread.sleep(t*1000);
			} catch (InterruptedException ignored) {

			}

			changeState(State.CONNECTING);

			try {
				conn = new Socket(server, port);
				conn.getOutputStream().write(this.client.getPackager().packHello());
				changeState(State.CONNECTED);
			} catch (IOException e) {
				exceptionOccured(e,true);
				changeState(State.FAILED);
			}
			t=Math.min(2*t,maxRetryInterval);
		}
		//TODO? trigger client
	}

	private void exceptionOccured(Exception e) {
		exceptionOccured(e, false);
	}

	private void exceptionOccured(Exception e, boolean onlyTriggerStateChange){
		if(s==State.NO_CONNECTION)return;

		this.lastException=e;
		changeState(State.EXCEPTION);

		if(onlyTriggerStateChange)return;
		
		if(this.maxRetryInterval<=0){
			changeState(State.NO_CONNECTION);
		}else{
			changeState(State.FAILED);

		}
	}

	private void changeState(State newState){changeState(newState,false);}
	private void changeState(State newState, boolean silent){
		this.s=newState;
		if(!silent)
			listener.handleStateChange(newState);
	}

	public State getState(){
		return s;
	}

}
