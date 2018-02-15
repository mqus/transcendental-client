package transcendental.client.lib;

import java.io.IOException;

public class ConnectionLostException extends IOException {
	/**
	 * Constructs an {@code IOException} with the specified cause and a
	 * detail message of {@code (cause==null ? null : cause.toString())}
	 * (which typically contains the class and detail message of {@code cause}).
	 * This constructor is useful for IO exceptions that are little more
	 * than wrappers for other throwables.
	 *
	 * @param cause The cause (which is saved for later retrieval by the
	 *              {@link #getCause()} method).  (A null value is permitted,
	 *              and indicates that the cause is nonexistent or unknown.)
	 * @since 1.6
	 */
	public ConnectionLostException(Throwable cause) {
		super("Connection lost",cause);
	}

	/**
	 * Constructs an {@code IOException} with {@code null}
	 * as its error detail message.
	 */
	public ConnectionLostException() {
		super("Connection lost");
	}
}
