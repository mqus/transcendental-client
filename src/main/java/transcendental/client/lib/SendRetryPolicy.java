package transcendental.client.lib;

import java.awt.datatransfer.Transferable;

/**
 * Created by markus on 31.01.17.
 */
public interface SendRetryPolicy {

	/**Determines if a Retry of a <b>Copy</b>Package is performed if the Connection was interrupted.
	 * @param t the Content of the Clipboard which will be retransferred
	 * @return a negative Value if no retry should be performed, or the amount of milliseconds to wait between each retry
	 */
	int shouldRetrySendingCopy(Transferable t);

	/**Determines if a Retry of a <b>Request</b>Package is performed if the Connection was interrupted.
	 * @return a negative Value if no retry should be performed, or the amount of milliseconds to wait between each retry
	 */
	int shouldRetrySendingRequest();

	/**Determines if a Retry of a <b>Data</b>Package is performed if the Connection was interrupted.
	 * @param t the Content of the Clipboard which will be retransferred
	 * @return a negative Value if no retry should be performed, or the amount of milliseconds to wait between each retry
	 */
	int shouldRetrySendingData(Transferable t);


}

class SimpleRetryPolicy implements SendRetryPolicy{
	final int shouldRetry;


	SimpleRetryPolicy(int shouldRetry) {
		this.shouldRetry = shouldRetry;
	}

	SimpleRetryPolicy(boolean shouldRetry) {
		this.shouldRetry = shouldRetry?500:-1;
	}

	/**
	 * Determines if a Retry of a <b>Copy</b>Package is performed if the Connection was interrupted.
	 *
	 * @param t the Content of the Clipboard which will be retransferred
	 * @return a negative Value if no retry should be performed, or the amount of milliseconds to wait between each retry
	 */
	@Override
	public int shouldRetrySendingCopy(Transferable t) {
		return shouldRetry;
	}

	/**
	 * Determines if a Retry of a <b>Request</b>Package is performed if the Connection was interrupted.
	 *
	 * @return a negative Value if no retry should be performed, or the amount of milliseconds to wait between each retry
	 */
	@Override
	public int shouldRetrySendingRequest() {
		return shouldRetry;
	}

	/**
	 * Determines if a Retry of a <b>Data</b>Package is performed if the Connection was interrupted.
	 *
	 * @param t the Content of the Clipboard which will be retransferred
	 * @return a negative Value if no retry should be performed, or the amount of milliseconds to wait between each retry
	 */
	@Override
	public int shouldRetrySendingData(Transferable t) {
		return shouldRetry;
	}
}
