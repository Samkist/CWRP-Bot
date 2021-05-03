package dev.samkist.renzhe.command.admin;

import dev.samkist.renzhe.Manager;
import dev.samkist.renzhe.command.lib.Command;
import dev.samkist.renzhe.command.lib.CommandContext;
import dev.samkist.renzhe.data.NoSuchMemberException;
import dev.samkist.renzhe.utils.ConfigManager;
import dev.samkist.renzhe.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GlobalBan implements Command {
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
			EmbedBuilder builder = ConfigManager.defaultEmbed().setDescription("**FATAL ERROR**").setColor(Color.RED);
			channel.sendMessage(builder.build()).queue();
		}
	}


	private void evaluate(Message message, String args) throws Exception {
		final Member member = message.getMember();
		final TextChannel channel = message.getTextChannel();
		final ArrayList<String> argsList = new ArrayList(Arrays.asList(args.split(" ")));
		if(!Utils.hasStaffPermission(member, getContext())) {
			message.getChannel().sendMessage(Utils.noPermissionEmbed(getContext()).build()).queue();
			return;
		}

		if(!message.getGuild().getId().equals(ConfigManager.getMainGuild().getId())) {
			EmbedBuilder builder = Utils.embedWithDescription("Please use this command in the main discord guild.").setColor(Color.red);
			channel.sendMessage(builder.build()).queue();
			return;
		}

		if(argsList.size() == 1) {
			throw new NoSuchMemberException("No such member!");
		}


		final String memberArg = argsList.get(0).replaceAll("[<!@>]", "");

		argsList.remove(0);

		User toBan = null;
		try {
			if (memberArg.contains("#")) {
				toBan = Manager.jda.getUserByTag(memberArg);
			} else {
				System.out.println("Trying to find user id with " + memberArg);
				toBan = Manager.jda.retrieveUserById(memberArg).complete();
			}
		} catch(Exception e) {
			channel.sendMessage(toBan.getAsTag()).queue();
			throw new NoSuchMemberException("");
		}

		try {
			Member b = ConfigManager.getMainGuild().getMemberById(toBan.getId());
			if(!Utils.isSuperiorRank(member, b)) {
				EmbedBuilder builder = Utils.embedWithDescription("You must be a higher rank than this user!");
				message.getChannel().sendMessage(builder.build()).queue();
			}
		} catch(Exception e) {

		}

		StringBuffer reasonBuffer = new StringBuffer();

		argsList.forEach(a -> reasonBuffer.append(a).append(" "));

		final String identifier = toBan.getAsTag();
		final User toBanUser = toBan;

		try {
			List<Guild> guilds = Manager.jda.getGuilds();
			guilds.forEach(guild -> guild.ban(toBanUser, 0, reasonBuffer.toString()).complete());
		} catch (Exception e) {
			throw new Exception("AHHHHH");
		}

		StringBuilder reasonBuilder = new StringBuilder(member.getAsMention())
				.append(" global banned ").append(identifier);
		if(argsList.size() > 0) {
			reasonBuilder.append(" for reason: ").append(reasonBuffer.toString());
		}

		EmbedBuilder builder = Utils.embedWithDescription(reasonBuilder.toString())
				.setImage(ConfigManager.banGif());
		channel.sendMessage(builder.build()).queue();
	}

	@Override
	public CommandContext getContext() {
		return ConfigManager.getCommandContextByName("administrative", "globalban");
	}
}
