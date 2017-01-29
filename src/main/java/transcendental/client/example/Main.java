package transcendental.client.example;

import transcendental.client.lib.Connection;
import transcendental.client.lib.State;
import transcendental.client.lib.StateChangeListener;

/**
 * Created by markus on 26.01.17.
 */
public class Main implements StateChangeListener {
	public static void main(String[] args) {
		Connection conn = new Connection("localhost",19192);

	}

	@Override
	public void handleStateChange(State NewState) {

	}
}
