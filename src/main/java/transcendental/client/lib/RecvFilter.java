package transcendental.client.lib;

import java.awt.datatransfer.DataFlavor;

/**
 * Created by markus on 28.01.17.
 */
public interface RecvFilter {
	RecvFilter ACCEPT_ALL = new RecvFilter() {
		@Override
		public boolean shouldRecv(DataFlavor[] list) {
			return true;
		}
	};
	RecvFilter DENY_ALL = new RecvFilter() {
		@Override
		public boolean shouldRecv(DataFlavor[] list) {
			return false;
		}
	};

	/**
	 * The implementing Class should decide, provided a List of flavors, if it wants to accept that Clipboard Content or not.
	 * @param list the given List of Flavors
	 * @return true, if the data will be accepted and false, if not.
	 */
	boolean shouldRecv(DataFlavor[] list);
}
