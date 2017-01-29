package transcendental.client.lib;

/**
 * Created by markus on 26.01.17.
 */
public enum State {
	//The client is attempting to connect to the server.
	CONNECTING,

	//The client is connected to the server.
	CONNECTED,

	//The connection Attempt FAILED.
	FAILED,

	//The client is not connected and will not try to attempt to connect.
	NO_CONNECTION,

	//The Connection was lost.
	CONNECTION_LOST,

	//An Exception was thrown
	EXCEPTION,


	//The client requested for clipboard data but has no answer yet
	//REQUEST_PENDING,


}
