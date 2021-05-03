package dev.samkist.renzhe.command.user;

import dev.samkist.renzhe.utils.ConfigManager;
import dev.samkist.renzhe.command.lib.Command;
import dev.samkist.renzhe.command.lib.CommandContext;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class Say implements Command {
	/**
	 * This is the method called on to execute the command.
	 *
	 * @param message The message which triggered the command.
	 * @param args    The arguments of the commands.
	 * @since 1.0.0
	 */
	@Override
	public void execute(Message message, String args) {
		final TextChannel channel = message.getTextChannel();
		final String asMentioned = message.getMember().getAsMention();
		channel.sendMessage(
				ConfigManager.defaultEmbed().setDescription("Message from: " + asMentioned + "\n" + args).build()
		).queue(s -> message.delete().queue());
	}

	@Override
	public CommandContext getContext() {
		return ConfigManager.getCommandContextByName("public", "say");
	}
}
