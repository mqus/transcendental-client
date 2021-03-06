package transcendental.client.lib;

/**
 * Created by markus on 26.01.17.
 */
public enum ConnState {

	/**
	 * The client is attempting to connect to the server.
	 */
	CONNECTING,

	/**
	 * The client is connected to the server.
	 */
	CONNECTED,

	/**
	 * The connection Attempt FAILED.
	 */
	FAILED,

	/**
	 * The client is not connected and will not try to attempt to connect.
	 */
	NO_CONNECTION,

	/**
	 * The Connection was lost.
	 */
	CONNECTION_LOST,

	/**
	 * An Exception was thrown
	 */
	EXCEPTION,

	/**
	 * The connection is currently being closed.
	 */
	DISCONNECTING,

}
