package transcendental.client.lib;

import java.awt.*;
import java.awt.datatransfer.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Semaphore;

/**
 * Created by markus on 26.01.17.
 */
public class AWTAdaptor implements FlavorListener, Transferable, ClipboardOwner {
	//config

	private StateChangeListener listener = new DefaultListener(true);
	private SendFilter sendPolicy = SendFilter.ACCEPT_ALL;
	private RecvFilter recvPolicy = RecvFilter.ACCEPT_ALL;
	private ClientState s = ClientState.INIT;


	//runtimeVars
	private Packager packager;
	private Clipboard clipboard;
	private int lastClipboardHolder = 0;
	private boolean isClipboardContentFromOtherClient = false;
	private DataFlavor[] lastflavors = new DataFlavor[0];
	private byte[] data;
	private Connection conn;
	private Semaphore waitForRecv = new Semaphore(0);
	private DataFlavor requestFlavor;
	private boolean eager_copy;

	public AWTAdaptor(Packager packager, Connection conn) {
		this.conn = conn;
		this.packager = packager;
		conn.bind(this.packager);
		this.clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		this.eager_copy = isClipboardEager();
	}

	private boolean isClipboardEager() {
		String name = System.getProperty("os.name").toLowerCase();
		if (name.contains("windows"))
			return true;
		if (name.contains("linux"))
			return false;
		//TODO moar os
		return false;
	}

	public AWTAdaptor setStateChangeListener(StateChangeListener listener) {
		if(listener == null)
			this.listener = StateChangeListener.VERBOSE;
		else
			this.listener = listener;
		return this;
	}

	Packager getPackager() {
		return packager;
	}


	/**
	 * starts the (inifinite) Client Loop, which occasionally calls StateChangedListener to notify the Program running the client.
	 * This method should by spawned in a new thread to run the adaptor in the background.
	 */
	public void connectAndRun() {
		boolean ok = conn.open();
		if(!ok) return;
		//MAYBE ok this way
		this.clipboard.addFlavorListener(this);
		recvLoop();

	}

	public AWTAdaptor setClipboard(Clipboard cb) {
		if (cb == null)
			cb = Toolkit.getDefaultToolkit().getSystemClipboard();

		this.clipboard = cb;
		return this;
	}


	public void disconnect() {
		//MAYBE disconnect properly

		this.clipboard.removeFlavorListener(this);
		this.conn.disconnect();
		if(waitForRecv.hasQueuedThreads())
			waitForRecv.release();
	}

	/**
	 * A simple function for StateChangeListeners to read the current requested flavor of the clipboard.
	 * (only possible if the new State is DATA_REQUESTED or REQUEST_PENDING
	 * @return the Flavor of the current Request
	 */
	public DataFlavor getCurrentRequestFlavor() {
		return requestFlavor;
	}

	private void changeState(ClientState newState) {
		if(s == newState) return;
		s = newState;
		listener.handleClientStateChange(newState);
	}

	private void recvLoop() {
		changeState(ClientState.INIT);
		while(true) {
			Package pkg = conn.recv();
			if(pkg == null) {
				if(conn.getState() == ConnState.DISCONNECTING || conn.getState() == ConnState.NO_CONNECTION) {
					//if disconnect is called from the outside, it will be called again here.
					disconnect();
					return;
				}
				if(conn.getState() == ConnState.FAILED)
					conn.waitForConnection();
				continue;
			}

			switch(pkg.getType()) {
				case COPY:
					//Another Client has provided the type of his clipboard content!
					//Lets see what he got...
					DataFlavor[] flavors = Util.deserializeFlavors(pkg.getContent());
					if(recvPolicy.shouldRecv(flavors)) {
						lastflavors = flavors;
						lastClipboardHolder = pkg.getClientID();
						if (eager_copy) {
							isClipboardContentFromOtherClient = true;
							changeState(ClientState.REQUEST_POSSIBLE);
							new Thread(new Runnable() {
								@Override
								public void run() {
									clipboard.setContents(AWTAdaptor.this, AWTAdaptor.this);
								}
							}).start();
						} else {
							isClipboardContentFromOtherClient = true;
							clipboard.setContents(this, this);
							changeState(ClientState.REQUEST_POSSIBLE);
						}
					}
					break;
				case DATA:
					if(s == ClientState.REQUEST_PENDING) {
						data = pkg.getContent();
						changeState(ClientState.DATA_RECEIVED);
					}

					break;
				case REQUEST:
					if(pkg.getClientID() == 0) {
						//Own Data Request Failed
						data = null;
						lastflavors = null;
						changeState(ClientState.REQUEST_FAILED);
					} else {
						if(s != ClientState.DATA_ON_THIS_CLIENT)
							replyWithReject(pkg.getClientID(), null);
						requestFlavor = Util.deserializeFlavor(pkg.getContent());
						changeState(ClientState.DATA_REQUESTED);

						if(requestFlavor != null && clipboard.isDataFlavorAvailable(requestFlavor)) {
							try {
								byte[] data = Util.serializeData(clipboard.getContents(this), requestFlavor);
								requestFlavor=null;
								conn.reliableSend(getPackager().packData(data, pkg.getClientID()), false);
							} catch (IOException | UnsupportedFlavorException e) {
								//If errors occured while trying to copy from the clipboard,
								//reply with the List minus the current flavour.
								e.printStackTrace();
								replyWithReject(pkg.getClientID(), requestFlavor);

							}
							requestFlavor=null;

						} else {
							replyWithReject(pkg.getClientID(), requestFlavor);
						}
						changeState(ClientState.DATA_ON_THIS_CLIENT);
					}
					break;
				case REJECT:
					//ignore the package if no one is waiting anymore
					if(s == ClientState.REQUEST_PENDING) {
						if(pkg.getContent().length == 0) {
							//Own Data Request Failed
							data = null;
							lastflavors = null;
							changeState(ClientState.REQUEST_FAILED);
						} else {
							lastflavors = Util.deserializeFlavors(pkg.getContent());
							changeState(ClientState.REQUEST_REJECTED);
						}
					}
					break;
				case TEXT:
					//TODO implement TEXT package
				case NULL:
					//Bad package; discard
			}
			if(waitForRecv.hasQueuedThreads())
				waitForRecv.release();
		}
	}

	private void replyWithReject(int to, DataFlavor naFlavor) {
		byte[] data;
		if (naFlavor == null)
			data = new byte[0];
		else {
			ArrayList<DataFlavor> flavors = new ArrayList<>(Arrays.asList(clipboard.getAvailableDataFlavors()));
			flavors.remove(naFlavor);
			data = Util.serializeFlavors(flavors.toArray(new DataFlavor[0]));
		}
		conn.reliableSend(getPackager().packReject(data, to), false);
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

		if(isClipboardContentFromOtherClient)
			//The Client just took over the Clipboard, so this Callback shouldn't be executed.
			return;

		if(this.conn.getState() == ConnState.CONNECTED)
			changeState(ClientState.DATA_ON_THIS_CLIENT);
		if(sendPolicy.shouldSend(clipboard.getContents(this))) {
			DataFlavor[] flavors = clipboard.getAvailableDataFlavors();
			byte[] data = Util.serializeFlavors(flavors);

			conn.reliableSend(getPackager().packCopy(data), false);

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
		isClipboardContentFromOtherClient = false;
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
		for(DataFlavor flavorB : lastflavors) {
			if(flavorA.equals(flavorB)) return true;
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
	synchronized public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		if(s==ClientState.INIT)
			throw new IOException("Request Failed (reason: remote clipboard lost)");
		byte[] data = Util.serializeFlavor(flavor);
		boolean success = conn.send(getPackager().packRequest(data, lastClipboardHolder));
		if(!success)
			throw new IOException("Request Failed (reason: send failed)");
		requestFlavor=flavor;
		changeState(ClientState.REQUEST_PENDING);
		requestFlavor=null;
		try {
			waitForRecv.acquire();
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
		if(s == ClientState.DATA_RECEIVED) {
			Object o = Util.deserializeData(this.data);
			changeState(ClientState.REQUEST_POSSIBLE);
			return o;
		} else if(s == ClientState.REQUEST_REJECTED) {
			changeState(ClientState.REQUEST_POSSIBLE);
			throw new UnsupportedFlavorException(flavor);
		} else if(conn.getState() == ConnState.NO_CONNECTION || conn.getState() == ConnState.DISCONNECTING) {
			throw new IOException("Request Failed (reason: disconnect)");
		} else {
			ClientState cs = s;
			changeState(ClientState.INIT);
			throw new IOException("Request Failed (reason: reject," + cs + ")");
		}
	}
}


