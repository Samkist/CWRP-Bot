package dev.samkist.renzhe.command.user;

import dev.samkist.renzhe.command.lib.Command;
import dev.samkist.renzhe.command.lib.CommandContext;
import dev.samkist.renzhe.command.lib.Evaluate;
import dev.samkist.renzhe.utils.ConfigManager;
import net.dv8tion.jda.api.entities.Message;

public class Suggest implements Command {
	@Override
	public Evaluate<Message, String> getEvaluate() {
		return (message, args) -> {

		};
	}

	@Override
	public CommandContext getContext() {
		return ConfigManager.getCommandContextByName("public", "suggest");
	}
}
