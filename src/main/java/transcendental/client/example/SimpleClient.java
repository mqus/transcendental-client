package transcendental.client.example;

import transcendental.client.lib.*;

import java.security.InvalidKeyException;

/**
 * Created by markus on 26.01.17.
 */
public class SimpleClient implements StateChangeListener {
	private static final String ver = "v0.7.1";

	// for coloring the output
	private static final String ANSI_RESET = "\u001B[0m";
	private static final String ANSI_BLACK = "\u001B[30m";
	private static final String ANSI_RED = "\u001B[31m";
	private static final String ANSI_GREEN = "\u001B[32m";
	private static final String ANSI_YELLOW = "\u001B[33m";
	private static final String ANSI_BLUE = "\u001B[34m";
	private static final String ANSI_PURPLE = "\u001B[35m";
	private static final String ANSI_CYAN = "\u001B[36m";
	private static final String ANSI_WHITE = "\u001B[37m";


	private Connection conn;
	private ClipboardAdaptor clipboardAdaptor;

	public SimpleClient(String server, int port, String passwd) throws InvalidKeyException {
		//setup
		conn = new ServerConnection(server, port);
		conn.setStateChangeListener(this);
		Packager pkger = new Packager(passwd);
		clipboardAdaptor = new ClipboardAdaptor(pkger, conn);
		clipboardAdaptor.setStateChangeListener(this);

	}

	public void go() {
		clipboardAdaptor.connectAndRun();
	}

	@Override
	public void handleConnStateChange(ConnState newState) {

		System.out.println(ANSI_BLUE + "-connstateChanged: " + newState + ANSI_RESET);
		if(newState == ConnState.EXCEPTION && !(conn.getLastException() instanceof ConnectionLostException)) {
			System.out.print(ANSI_RED);
			conn.getLastException().printStackTrace();
			System.out.print(ANSI_RESET);
		}
	}

	@Override
	public void handleClientStateChange(ClientState newState) {
		String additionals="";
		if(newState == ClientState.REQUEST_PENDING || newState == ClientState.DATA_REQUESTED)
			additionals = ", flavor: "+ clipboardAdaptor.getCurrentRequestFlavor();
		System.out.println(ANSI_GREEN + "- clientstateChanged: " + newState + additionals + ANSI_RESET);
	}


	public static void main(String[] args) throws InvalidKeyException {
		//defaults:
		String server = "localhost", passwd = "RaumRaumRaumRaum";
		int port = 19192;

		for(int i = 0; i < args.length; i++) {
			if(args[i].startsWith("-")) {
				switch(args[i]) {
					case "-p":
					case "--port":
						port = Integer.valueOf(args[++i]);
						break;
					case "-s":
					case "--server":
						server = args[++i];
						if(server.contains(":")) {
							//take the argument as a server:port option:
							String[] a = server.split(":", 2);
							server = a[0];
							if(a.length>1)
								port = Integer.valueOf(a[1]);
						}
						break;
					case "-r":
					case "-k":
					case "--password":
					case "--key":
					case "--room":
						passwd = args[++i];
						break;
					case "-h":
					case "--help":
						showHelp();
						return;
					case "-v":
					case "--version":
						showVersion(false);
						return;
				}
			} else {
				//take the argument as a server:port option:
				String[] a = args[i].split(":", 2);
				if (!a[0].isEmpty())
					server = a[0];
				if(a.length>1)
					port = Integer.valueOf(a[1]);
			}

		}

		SimpleClient sc = new SimpleClient(server, port, passwd);
		sc.go();
	}


	private static void showHelp() {
		showVersion(true);
		System.out.println("Usage: transcendental-client [OPTIONS] [<server>:<port>]");
		System.out.println("  -h\t--help\t \t outputs this help");
		System.out.println("  -v\t--version\t \t outputs the version");
		System.out.println("  -s\t--server\t<server>[:<port>]\t specifies the server which distributes the data, default:localhost");
		System.out.println("  -p\t--port\t<port>\t specifies the server port, default:19192");
		System.out.println("  -r|-k\t--password | --room | --key\t<passwd>\t specifies the encryption keyword and at the same time the room*, default:RaumRaumRaumRaum");
		System.out.println("\n   (*) a room is the separation unit in which the server distributes the clipboard content, there can be multiple rooms on a server.");
	}

	private static void showVersion(boolean extended) {
		System.out.println("Transcendental-SimpleClient " + ver);
		if(extended) {
			System.out.println("\ta simple deamon-like Client for sharing the local Clipboard with other Devices.");
		}
	}

}
