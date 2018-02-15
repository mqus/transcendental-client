package transcendental.client.lib;

/**
 * Created by markus on 26.01.17.
 */
public enum ClientState {

	/**
	 * The client currently has no clipboard content, not remotely and not local
	 */
	INIT,

	/**
	 * The client was notified of data on another client
	 */
	REQUEST_POSSIBLE,

	/**
	 * The client requested for clipboard data but has no answer yet
	 */
	REQUEST_PENDING,

	/**
	 * The client received a REJECT-Message, possibly because the flavor was not available(anymore)
	 */
	REQUEST_REJECTED,

	/**
	 * The client was unable to transmit the REQUEST safely
	 */
	REQUEST_FAILED,

	/**
	 * The client received the requested Data
	 */
	DATA_RECEIVED,

	/**
	 * The client has become the owner of the shared clipboard
	 */
	DATA_ON_THIS_CLIENT,

	/**
	 * This client was requested for its clipboard contents by another client
	 */
	DATA_REQUESTED,
}
