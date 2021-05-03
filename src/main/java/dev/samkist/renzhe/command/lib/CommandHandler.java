package dev.samkist.renzhe.command.lib;

import net.dv8tion.jda.api.entities.Message;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * The {@link CommandHandler} which deals with the registered {@link Command}s.
 *
 * @author Comportment
 * @since 1.0.0
 */
public class CommandHandler {

	/**
	 * A set of all of the commands that this {@link CommandHandler} has registered.
	 *
	 * @see #getCommands()
	 * @since 1.0.0
	 */
	private Set<Command> commands = new HashSet<>();

	/**
	 * A method to register {@link Command}s with this {@link CommandHandler}.
	 *
	 * @param commands The {@link Command}s to register.
	 * @see #registerCommand(Command)
	 * @since 1.0.0
	 */
	public void registerCommands(Set<Command> commands) {
		this.commands.addAll(commands);
	}

	/**
	 * A method to register {@link Command}s with this {@link CommandHandler}.
	 *
	 * @param commands The {@link Command}s to register.
	 * @see #registerCommand(Command)
	 * @see #registerCommands(Set)
	 * @since 1.0.1
	 */
	public void registerCommands(Command... commands) {
		Collections.addAll(this.commands, commands);
	}

	/**
	 * A method to register a {@link Command} with this {@link CommandHandler}.
	 *
	 * @param command The {@link Command} to register.
	 * @see #registerCommands(Set)
	 * @since 1.0.1
	 */
	public void registerCommand(Command command) {
		this.registerCommands(command);
	}

	/**
	 * A method to unregister {@link Command}s with this {@link CommandHandler}.
	 *
	 * @param commands The commands to unregister.
	 * @see #unregisterCommand(Command)
	 * @see #unregisterCommands(Set)
	 * @since 1.0.1
	 */
	public void unregisterCommands(Set<Command> commands) {
		this.commands.removeAll(commands);
	}

	/**
	 * A method to unregister {@link Command}s with this {@link CommandHandler}.
	 *
	 * @param commands The commands to unregister.
	 * @see #unregisterCommand(Command)
	 * @see #unregisterCommands(Set)
	 * @since 1.0.1
	 */
	public void unregisterCommands(Command... commands) {
		this.commands.removeAll(Arrays.asList(commands));
	}

	/**
	 * A method to unregister a {@link Command} with this {@link CommandHandler}.
	 *
	 * @param command The command to unregister.
	 * @see #unregisterCommands(Set)
	 * @see #unregisterCommands(Command...)
	 * @since 1.0.1
	 */
	public void unregisterCommand(Command command) {
		this.unregisterCommands(command);
	}

	/**
	 * A method to get all of the {@link Command}s registered with this {@link CommandHandler}
	 *
	 * @return All of the commands registered with this command handler.
	 * @since 1.0.1
	 */
	public Set<Command> getCommands() {
		return commands;
	}

	/**
	 * Method which attempts to find a {@link Command} from the given trigger
	 *
	 * @param trigger The trigger of the command to find.
	 * @return The {@link Command} that was found, sometimes <code>null</code>
	 * @since 1.0.0
	 */
	public Command findCommand(String trigger) {
		return commands.stream().filter(command -> {
			CommandContext cd = command.getContext();
			return cd.triggers().stream().filter(t -> t.equalsIgnoreCase(trigger)).findAny().isPresent();
		}).findFirst().orElse(null);
	}

	/**
	 * Method which attempts to execute the given {@link Command}.
	 *
	 * @param command The {@link Command} to execute.
	 * @param message The {@link Message} which triggered the command.
	 * @param args    The arguments of the command.
	 * @since 1.0.0
	 */
	public void execute(Command command, Message message, String args) {
		CommandContext cd = command.getContext();
		if (cd == null) return;
		command.execute(message, args.trim());
	}

	/**
	 * A method which calls {@link #findCommand(String)}, and then {@link #execute(Command, Message, String)} if the found {@link Command} is not <code>null</code>
	 *
	 * @param trigger The trigger of the command.
	 * @param message The {@link Message} which triggered the command.
	 * @param args    The args of the command.
	 * @see #findCommand(String)
	 * @see #execute(Command, Message, String)
	 * @since 1.0.1
	 */
	public void findAndExecute(String trigger, Message message, String args) {
		Command command = this.findCommand(trigger);
		if (command == null || command.getContext() == null) return;
		this.execute(command, message, args);
	}
}
