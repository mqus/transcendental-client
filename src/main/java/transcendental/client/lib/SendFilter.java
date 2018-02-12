package transcendental.client.lib;

import java.awt.datatransfer.Transferable;

/**
 * Created by markus on 28.01.17.
 */
public interface SendFilter {
	SendFilter ACCEPT_ALL = new SendFilter() {
		@Override
		public boolean shouldSend(Transferable t) {
			return true;
		}
	};
	SendFilter DENY_ALL = new SendFilter() {
		@Override
		public boolean shouldSend(Transferable t) {
			return false;
		}
	};

	boolean shouldSend(Transferable t);
}
