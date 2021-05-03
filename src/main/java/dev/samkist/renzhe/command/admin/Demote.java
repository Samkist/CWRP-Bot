package dev.samkist.renzhe.command.admin;

import dev.samkist.renzhe.data.NoSuchMemberException;
import dev.samkist.renzhe.utils.ConfigManager;
import dev.samkist.renzhe.command.lib.Command;
import dev.samkist.renzhe.command.lib.CommandContext;
import dev.samkist.renzhe.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

public class Demote implements Command {
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
		try {
			evaluate(message, args);
		} catch (NoSuchMemberException ex) {
			EmbedBuilder builder = Utils.noSuchPlayerEmbed(getContext());
			channel.sendMessage(builder.build()).queue();
		} catch (Exception e) {
			e.printStackTrace();
			EmbedBuilder builder = ConfigManager.defaultEmbed().setDescription(Optional.ofNullable(e.getMessage()).orElse("**FATAL ERROR**")).setColor(Color.RED);
			channel.sendMessage(builder.build()).queue();
		}
	}


	private void evaluate(Message message, String args) throws Exception {
		final Member member = message.getMember();
		final Guild guild = member.getGuild();
		final ArrayList<String> argsList = new ArrayList(Arrays.asList(args.split(" ")));
		if(!Utils.hasStaffPermission(member, getContext())) {
			message.getChannel().sendMessage(Utils.noPermissionEmbed(getContext()).build()).queue();
			return;
		}

		if(argsList.size() == 0) {
			throw new NoSuchMemberException();
		}

		final String memberArg = argsList.get(0).replaceAll("[<!@>]", "");

		Member toDemote = null;

		try {
			toDemote = guild.retrieveMemberById(memberArg).complete();
		} catch(Exception e) {
			throw new NoSuchMemberException();
		}

		if(toDemote.getId().equals(member.getId())) {
			throw new Exception("Don't be so hard on yourself!");
		}

		if(!Utils.isSuperiorRank(member, toDemote)) {
			throw new Exception("You must be a higher rank than this member!");
		}

	}

	@Override
	public CommandContext getContext() {
		return ConfigManager.getCommandContextByName("administrative", "demote");
	}
}
