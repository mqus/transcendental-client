package transcendental.client;

import java.nio.channels.AlreadyBoundException;

/**
 * Created by markus on 26.01.17.
 */
public enum State {
	Connecting,
	Connected,
	Failed,
	NoConnection,
}
