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

	boolean shouldRecv(DataFlavor[] list);
}
