package dev.samkist.renzhe.command.lib;

import dev.samkist.renzhe.utils.Utils;
import net.dv8tion.jda.api.entities.Message;

/**
 * This represents a generic command.
 *
 * @author Comportment
 * @since 1.0.0
 */
public interface Command extends Comparable<Command> {

	/**
	 * This is the method called on to execute the command.
	 *
	 * @param message The message which triggered the command.
	 * @param args    The arguments of the commands.
	 * @since 1.0.0
	 */
	default void execute(Message message, String args) {
		Utils.applyEvaluate(getContext(), message, args, getEvaluate());
	}

	Evaluate<Message, String> getEvaluate();


	CommandContext getContext();

	/**
	 * Returns the value of the attribute from the key given.
	 *
	 * @param key The key of the {@link me.diax.comportment.jdacommand.CommandAttribute} to find.
	 * @return The value of the attribute, could be <code>null</code>.
	 * @since 1.0.2
	 */
	default String getAttributeValueFromKey(String key) {
		if (!hasAttribute(key)) return null;
		return getContext().attributes().stream().filter(ca -> ca.key().equals(key)).findFirst().orElse(null).key();
	}

	/**
	 * Returns if the command has an attribute with the matching key.
	 *
	 * @param key The key of the attribute
	 * @return True if the command has the attribute, false if it does not.
	 * @since 1.0.2
	 */
	default boolean hasAttribute(String key) {
		return getContext().attributes().stream().anyMatch(ca -> ca.key().equals(key));
	}

	@Override
	default int compareTo(Command that) {
		return this.getContext().name().compareTo(that.getContext().name());
	}
}
