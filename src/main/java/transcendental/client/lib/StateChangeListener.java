package transcendental.client.lib;

/**
 * Created by markus on 26.01.17.
 */
public interface StateChangeListener {
	StateChangeListener SILENT = new DefaultListener(false);
	StateChangeListener VERBOSE = new DefaultListener(true);
	void handleStateChange(State NewState);
}
class DefaultListener implements StateChangeListener {
	private boolean verbose;

	DefaultListener(boolean verbose) {

		this.verbose = verbose;
	}

	@Override
	public void handleStateChange(State NewState) {
		if(verbose) System.out.println("New State:"+NewState);

		//Do nothing
	}
}

