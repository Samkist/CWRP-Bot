package dev.samkist.renzhe.command.user;

import dev.samkist.renzhe.command.lib.Command;
import dev.samkist.renzhe.command.lib.CommandContext;
import dev.samkist.renzhe.command.lib.MessageHandler;
import dev.samkist.renzhe.utils.ConfigManager;
import dev.samkist.renzhe.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;
import java.util.stream.Collectors;

public class RoleCommands implements Command {
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
		List<Command> sortedCommands = MessageHandler.commandHandler.getCommands().stream().sorted((c1, c2) -> {
			final Integer c1Index = Utils.permissionIndex(c1.getContext());
			final Integer c2Index = Utils.permissionIndex(c2.getContext());
			return c1Index.compareTo(c2Index);
		}).collect(Collectors.toList());
		EmbedBuilder builder = ConfigManager.defaultEmbed();
		sortedCommands.forEach(c -> {
			try {
				if(c.getContext().visible()) {
					final String mention = Utils.roleByContext(c.getContext()).getAsMention();
					builder.addField(c.getContext().name().toUpperCase(), mention, true);
				}
			} catch(Exception e) {

			}
		});
		channel.sendMessage(builder.build()).queue();
	}

	@Override
	public CommandContext getContext() {
		return ConfigManager.getCommandContextByName("public", "rolecommands");
	}
}
