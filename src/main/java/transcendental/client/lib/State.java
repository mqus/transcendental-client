package transcendental.client.lib;

/**
 * Created by markus on 26.01.17.
 */
public enum State {
	//The client is attempting to connect to the server.
	Connecting,

	//The client is connected to the server.
	Connected,

	//The connection Attempt Failed.
	Failed,

	//The client is not connected and will not try to attempt to connect.
	NoConnection,

	//?
	Exception,

}
