package transcendental.client.lib;

import com.sun.org.apache.bcel.internal.generic.NEW;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.FlavorEvent;
import java.awt.datatransfer.FlavorListener;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by markus on 26.01.17.
 */
public class Client implements FlavorListener {
	//config

	private int port=19192;
	private String server="localhost",room;
	private Exception lastException=null;
	private StateChangeListener listener=new DefaultListener(true);


	//runtimeVars
	private Socket conn;
	private Clipboard clipboard;
	private State s;

	public Client(String room) {
		this.room=room;
		this.clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		this.clipboard.addFlavorListener(this);
	}

	public Client setPort(int port) throws IllegalArgumentException{
		if(port>= (1<<16) || port<=0)
			throw new IllegalArgumentException("" + port + " is not a valid port number!");
		this.port = port;
		return this;
	}

	public Client setServer(String server) {
		this.server = server;
		return this;
	}

	public Client setRoom(String room) {
		this.room = room;
		return this;
	}

	public Client setStateChangeListener(StateChangeListener listener){
		if(listener==null)
			listener=new DefaultListener();
		else
			listener=listener;
		return this;
	}

	public Exception getLastException(){
		return this.lastException;
	}


	/**
	 * starts the (inifinite) Client Loop, which occasionally calls StateChangedListener to notify the Program running the client.
	 */
	public void connectAndRun(){
		boolean successfull=connectOnce();
		//TODO
		




	}


	private boolean connectOnce(){
		changeStateState.Connecting);

		try {
			conn = new Socket(server, port);
			conn.getOutputStream().write(Package.createHelloPackage(encrypt(room)));
		} catch (IOException e) {
			lastException=e;
			changeStateState.Failed);
			//TODO:what to do if Failed?
			return false;
		}
		changeStateState.Connected);
		return true;
	}

	public void disconnect(){this.disconnect(false);}

	public void disconnect(boolean silent){

	}

	private byte[] encrypt(String s){
		//TODO
		return s.getBytes();
	}

	/**
	 * Invoked when the target {@link Clipboard} of the listener
	 * has changed its available {@link DataFlavor}s.
	 * <p>
	 * Some notifications may be redundant &#151; they are not
	 * caused by a change of the set of DataFlavors available
	 * on the clipboard.
	 * For example, if the clipboard subsystem supposes that
	 * the system clipboard's contents has been changed but it
	 * can't ascertain whether its DataFlavors have been changed
	 * because of some exceptional condition when accessing the
	 * clipboard, the notification is sent to ensure from omitting
	 * a significant notification. Ordinarily, those redundant
	 * notifications should be occasional.
	 *
	 * @param e a <code>FlavorEvent</code> object
	 */
	@Override
	public void flavorsChanged(FlavorEvent e) {
		if(s == State.Connected){

		}
	}

	private void changeState(State newState){
		this.s=newState;
		listener.handleStateChange(State.Connecting);
	}
}

class DefaultListener implements StateChangeListener {
	private boolean verbose=false;

	public DefaultListener() {
	}

	public DefaultListener(boolean verbose) {

		this.verbose = verbose;
	}

	@Override
	public void handleStateChange(State NewState) {
		if(verbose) System.out.println("New State:"+NewState);

		//Do nothing
	}
}
