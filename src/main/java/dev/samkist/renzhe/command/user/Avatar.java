package dev.samkist.renzhe.command.user;

import dev.samkist.renzhe.command.lib.Command;
import dev.samkist.renzhe.command.lib.CommandContext;
import dev.samkist.renzhe.command.lib.Evaluate;
import dev.samkist.renzhe.utils.ConfigManager;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

public class Avatar implements Command {
	@Override
	public Evaluate<Message, String> getEvaluate() {
		return (message, args) -> {
			final Member member = message.getMember();

		};
	}



	@Override
	public CommandContext getContext() {
		return ConfigManager.getCommandContextByName("public", "avatar");
	}
}
