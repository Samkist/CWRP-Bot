package dev.samkist.renzhe.command.user;

import dev.samkist.renzhe.command.lib.Command;
import dev.samkist.renzhe.command.lib.CommandContext;
import dev.samkist.renzhe.command.lib.Evaluate;
import dev.samkist.renzhe.utils.ConfigManager;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class Say implements Command {

	@Override
	public Evaluate<Message, String> getEvaluate() {
		return (message, args) -> {
			final TextChannel channel = message.getTextChannel();
			final String asMentioned = message.getMember().getAsMention();
			channel.sendMessage(
					ConfigManager.defaultEmbed().setDescription("Message from: " + asMentioned + "\n" + args).build()
			).queue(s -> message.delete().queue());
		};
	}

	@Override
	public CommandContext getContext() {
		return ConfigManager.getCommandContextByName("public", "say");
	}
}
