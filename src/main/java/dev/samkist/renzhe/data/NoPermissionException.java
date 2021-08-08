package dev.samkist.renzhe.data;

import dev.samkist.renzhe.command.lib.CommandContext;

public class NoPermissionException extends Exception {

	private CommandContext context;

	/**
	 * Constructs a new exception with {@code null} as its detail message.
	 * The cause is not initialized, and may subsequently be initialized by a
	 * call to {@link #initCause}.
	 */
	public NoPermissionException() {
		super();
	}

	/**
	 * Constructs a new exception with the specified detail message.  The
	 * cause is not initialized, and may subsequently be initialized by
	 * a call to {@link #initCause}.
	 *
	 * @param message the detail message. The detail message is saved for
	 *                later retrieval by the {@link #getMessage()} method.
	 */
	public NoPermissionException(String message) {
		super(message);
	}

	public NoPermissionException(CommandContext context) {
		super("Context");
		this.context = context;
	}

	public CommandContext getContext() {
		return context;
	}
}
