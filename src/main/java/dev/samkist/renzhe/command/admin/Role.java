package dev.samkist.renzhe.command.admin;

import dev.samkist.renzhe.utils.ConfigManager;
import dev.samkist.renzhe.command.lib.Command;
import dev.samkist.renzhe.command.lib.CommandContext;
import net.dv8tion.jda.api.entities.Message;

public class Role implements Command {
	/**
	 * This is the method called on to execute the command.
	 *
	 * @param message The message which triggered the command.
	 * @param args    The arguments of the commands.
	 * @since 1.0.0
	 */
	@Override
	public void execute(Message message, String args) {

	}

	@Override
	public CommandContext getContext() {
		return ConfigManager.getCommandContextByName("administrative", "role");
	}
}
