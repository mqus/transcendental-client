package transcendental.client.lib;

import java.awt.*;
import java.awt.datatransfer.*;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.Socket;
import java.security.InvalidKeyException;

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
	private Clipboard clipboard;
	private int lastClipboardHolder=0;
	private boolean isClipboardContentFromMe=false;
	private DataFlavor[] lastflavors=new DataFlavor[0];
	private Connection conn;

	public Client(String room,Connection conn) throws InvalidKeyException {
		this.conn=conn;
		conn.bind(this);
		this.packager=new Packager(room);
		this.clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	}

	Packager getPackager() {
		return packager;
	}



	/**
	 * starts the (inifinite) Client Loop, which occasionally calls StateChangedListener to notify the Program running the client.
	 */
	public void connectAndRun(){
		boolean ok = conn.open();
		if(!ok) return;
			//MAYBE ok this way
		this.clipboard.addFlavorListener(this);
		recvLoop();

	}


	public void disconnect(){
		//TODO
		this.clipboard.removeFlavorListener(this);
		this.conn.disconnect();
	}


	public void recvLoop(){
		while(true){
			Package pkg = conn.recv();
			if(pkg == null){
				//TODO
			}else{
				//TODO
			}
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
		if(this.conn.getState() == State.CONNECTED && sendPolicy.shouldSend(clipboard.getContents(this)) ){

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


