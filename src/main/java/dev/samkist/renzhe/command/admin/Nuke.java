package dev.samkist.renzhe.command.admin;

import dev.samkist.renzhe.command.lib.Command;
import dev.samkist.renzhe.command.lib.CommandContext;
import dev.samkist.renzhe.utils.ConfigManager;
import dev.samkist.renzhe.utils.Utils;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class Nuke implements Command {
	/**
	 * This is the method called on to execute the command.
	 *
	 * @param message The message which triggered the command.
	 * @param args    The arguments of the commands.
	 * @since 1.0.0
	 */
	@Override
	public void execute(Message message, String args) {
		final Member member = message.getMember();
		if(!Utils.hasStaffPermission(member, getContext())) {
			message.getChannel().sendMessage(Utils.noPermissionEmbed(getContext()).build()).queue();
			return;
		}
		final TextChannel originalChannel = message.getTextChannel();
		originalChannel.createCopy().queue(success -> originalChannel.delete().queue(succ -> {
			final TextChannel logChannel = ConfigManager.getChannelByFriendlyName("log");
			final String modAsMention = member.getAsMention();
			final String channelAsMention = success.getAsMention();
			final String msg = modAsMention + " nuked channel" + channelAsMention;
			logChannel.sendMessage(ConfigManager.defaultEmbed().setDescription(msg).build()).queue();
		}));
	}

	@Override
	public CommandContext getContext() {
		return ConfigManager.getCommandContextByName("administrative", "nuke").visible(false);
	}
}
