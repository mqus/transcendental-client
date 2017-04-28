package transcendental.client.example;

import transcendental.client.lib.*;

import java.security.InvalidKeyException;

/**
 * Created by markus on 26.01.17.
 */
public class SimpleClient implements StateChangeListener{
	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_BLACK = "\u001B[30m";
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_YELLOW = "\u001B[33m";
	public static final String ANSI_BLUE = "\u001B[34m";
	public static final String ANSI_PURPLE = "\u001B[35m";
	public static final String ANSI_CYAN = "\u001B[36m";
	public static final String ANSI_WHITE = "\u001B[37m";



	Connection conn;
	Client client;

	public SimpleClient() throws InvalidKeyException {
		//setup
		conn = new Connection("localhost",19192);
		conn.setStateChangeListener(this);
		client = new Client("RaumRaumRaumRaum", conn);
		client.setStateChangeListener(this);

	}

	public void go(){
		client.connectAndRun();
	}

	@Override
	public void handleConnStateChange(ConnState newState) {

		System.out.println(ANSI_BLUE + "-connstateChanged: " + newState + ANSI_RESET);
		if(newState == ConnState.EXCEPTION){
			System.out.print(ANSI_RED);
			conn.getLastException().printStackTrace();
			System.out.print(ANSI_RESET);
		}
	}

	@Override
	public void handleClientStateChange(ClientState newState) {
		System.out.println(ANSI_GREEN + "- clientstateChanged: " + newState + ANSI_RESET);
	}


	public static void main(String[] args) throws InvalidKeyException {
		SimpleClient sc = new SimpleClient();
		sc.go();
	}

}
