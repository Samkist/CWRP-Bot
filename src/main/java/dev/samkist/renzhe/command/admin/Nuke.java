package dev.samkist.renzhe.command.admin;

import dev.samkist.renzhe.command.lib.Command;
import dev.samkist.renzhe.command.lib.CommandContext;
import dev.samkist.renzhe.command.lib.Evaluate;
import dev.samkist.renzhe.data.NoPermissionException;
import dev.samkist.renzhe.utils.ConfigManager;
import dev.samkist.renzhe.utils.Utils;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class Nuke implements Command {

	@Override
	public Evaluate<Message, String> getEvaluate() {
		return (message, args) -> {
			final Member member = message.getMember();
			if(!Utils.hasStaffPermission(member, getContext())) {
				throw new NoPermissionException();
			}
			final TextChannel originalChannel = message.getTextChannel();
			originalChannel.createCopy().queue(success -> originalChannel.delete().queue(succ -> {
				final TextChannel logChannel = ConfigManager.getChannelByFriendlyName("log");
				final String modAsMention = member.getAsMention();
				final String channelAsMention = success.getAsMention();
				final String msg = modAsMention + " nuked channel" + channelAsMention;
				logChannel.sendMessage(ConfigManager.defaultEmbed().setDescription(msg).build()).queue();
			}));
		};
	}


	@Override
	public CommandContext getContext() {
		return ConfigManager.getCommandContextByName("administrative", "nuke").visible(false);
	}
}
