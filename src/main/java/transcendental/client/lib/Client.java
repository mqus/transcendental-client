package transcendental.client.lib;

import java.awt.*;
import java.awt.datatransfer.*;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.Socket;

/**
 * Created by markus on 26.01.17.
 */
public class Client implements FlavorListener, Transferable, ClipboardOwner {
	//config

	private int port=19192;
	private String server="localhost";
	private Exception lastException=null;
	private StateChangeListener listener=new DefaultListener(true);
	private SendFilter sendPolicy=SendFilter.ACCEPT_ALL;
	private RecvFilter recvPolicy=RecvFilter.ACCEPT_ALL;


	//runtimeVars
	private Packager packager;
	private Socket conn;
	private Clipboard clipboard;
	private State s;
	private int lastClipboardHolder=0;
	private boolean isClipboardContentFromMe=false;
	private DataFlavor[] lastflavors=new DataFlavor[0];

	public Client(String room) {
		this.packager=new Packager(room);
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
		//TODO
		//disconnect if neccessary
		this.packager=new Packager(room);
		//reconnect if neccessary
		return this;
	}

	public Client setStateChangeListener(StateChangeListener listener){
		if(listener==null)
			listener=StateChangeListener.SILENT;
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
		changeState(State.CONNECTING);

		try {
			conn = new Socket(server, port);
			conn.getOutputStream().write(packager.packHello());
		} catch (IOException e) {
			lastException=e;
			changeState(State.FAILED);
			//TODO:what to do if FAILED?
			return false;
		}
		changeState(State.CONNECTED);
		return true;
	}

	public void disconnect(){this.disconnect(false);}

	public void disconnect(boolean silent){
		try {
			conn.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		changeState(State.NO_CONNECTION, silent);
	}

	public void sendPkg(byte[] pkg){
		//send pkg.
		//when an error occurs,
		//TODO

		try {
			conn.getOutputStream().write(pkg);
		} catch (IOException e) {
			lastException=e;
			try {
				conn.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			changeState(State.CONNECTION_LOST);
		}
	}

	public void recvLoop(){
		while(true){
			Reader r = null;
			try {
				r = new InputStreamReader(conn.getInputStream(), "UTF-8");
				while(true){
					Package pkg = packager.deserialize(r);
					switch(pkg.getType()){
						case COPY:
							//TODO Received Copy
							// deserialize Flavors
							// write to lastFlavors
							//save ClientID
							// clipoard.setContent(this,this)

							break;
						case DATA:
							//TODO Release Semaphore
							// if state is ok
							// set state+data+currentLocalFlavor
							break;
						case REQUEST:
							//TODO reply with DATA/REJECT
							//check if flavor is ok (with clipboard)
							//get Data/Flavors per clipboard
							//Serialize DATA/Flavors
							//send Data/Reject
							break;
						case REJECT:
							//TODO Release Semaphore
							// if state is ok
							// set state + flavors
							break;
						case TEXT:
							//TODO
						//case TEXT:
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}



/*	public boolean sendPkg(Package pkg, boolean silent){
		conn.getOutputStream().write();

	}*/

	private void changeState(State newState){changeState(newState,false);}
	private void changeState(State newState, boolean silent){
		this.s=newState;
		if(!silent)
			listener.handleStateChange(State.CONNECTING);
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
		if(s == State.CONNECTED && sendPolicy.shouldSend(clipboard.getContents(this)) ){

			DataFlavor[] flavors = clipboard.getAvailableDataFlavors();
			byte[] data=Util.serializeFlavors(flavors);

			byte[] pkg=packager.packCopy(data);
			//TODO Sendmess

		}
	}


	/**
	 * Notifies this object that it is no longer the clipboard owner.
	 * This method will be called when another application or another
	 * object within this application asserts ownership of the clipboard.
	 *
	 * @param clipboard the clipboard that is no longer owned
	 * @param contents  the contents which this owner had placed on the clipboard
	 */
	@Override
	public void lostOwnership(Clipboard clipboard, Transferable contents) {
		isClipboardContentFromMe=false;
	}

	/**
	 * Returns an array of DataFlavor objects indicating the flavors the data
	 * can be provided in.  The array should be ordered according to preference
	 * for providing the data (from most richly descriptive to least descriptive).
	 *
	 * @return an array of data flavors in which this data can be transferred
	 */
	@Override
	public DataFlavor[] getTransferDataFlavors() {
		return lastflavors;
	}

	/**
	 * Returns whether or not the specified data flavor is supported for
	 * this object.
	 *
	 * @param flavorA the requested flavor for the data
	 * @return boolean indicating whether or not the data flavor is supported
	 */
	@Override
	public boolean isDataFlavorSupported(DataFlavor flavorA) {
		//TODO:do Better
		for (DataFlavor flavorB : lastflavors) {
			if( flavorA.equals(flavorB))return true;
		}
		return false;
	}

	/**
	 * Returns an object which represents the data to be transferred.  The class
	 * of the object returned is defined by the representation class of the flavor.
	 *
	 * @param flavor the requested flavor for the data
	 * @throws IOException                if the data is no longer available
	 *                                    in the requested flavor.
	 * @throws UnsupportedFlavorException if the requested data flavor is
	 *                                    not supported.
	 * @see DataFlavor#getRepresentationClass
	 */
	@Override
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		//TODO request   ||throws IOException

		//TODO wait for incoming ||throws IOException
		// with semaphore to wait for an incoming package
		// with timeout

		//TODO parse incoming ||throws UnsupportedFlavorException




		return null;
	}
}


