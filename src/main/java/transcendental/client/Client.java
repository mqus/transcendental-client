package transcendental.client;

/**
 * Created by markus on 26.01.17.
 */
public class Client {
	int port=19192;
	String server="localhost",room;

	public Client(String room) {
		this.room=room;
		//TODO
	}

	public Client setPort(int port) {
		this.port = port;
		//TODO exception
		return this;
	}

	public Client setServer(String server) {
		this.server = server;
		return this;
	}

	public Client setRoom(String room) {
		this.room = room;
		return this;
	}


	public void connectAndRun(){
		//TODO evrything
	}

	public void disconnect(){this.disconnect(false);}

	public void disconnect(boolean silent){

	}
}
