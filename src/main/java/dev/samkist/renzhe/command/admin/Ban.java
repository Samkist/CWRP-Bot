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
import java.util.concurrent.TimeUnit;

public class Ban implements Command {
	/**
	 * This is the method called on to execute the command.
	 *
	 * @param message The message which triggered the command.
	 * @param args    The arguments of the commands.
	 * @since 1.0.0
	 */
	public void execute(Message message, String args) {
		final TextChannel channel = message.getTextChannel();
		try {
			evaluate(message, args);
		} catch (NoSuchMemberException ex) {
			EmbedBuilder builder = Utils.noSuchPlayerEmbed(getContext());
			channel.sendMessage(builder.build()).queue();
		} catch (Exception e) {
			e.printStackTrace();
			EmbedBuilder builder = ConfigManager.defaultEmbed().setDescription(e.getMessage()).setColor(Color.RED);
			channel.sendMessage(builder.build()).queue();
		}
	}


	private void evaluate(Message message, String args) throws NoSuchMemberException {
		final Member member = message.getMember();
		final TextChannel channel = message.getTextChannel();
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

		argsList.remove(0);

		User toBan = null;
		try {
			if (memberArg.contains("#")) {
				toBan = guild.getMemberByTag(memberArg).getUser();
			} else {
				toBan = Manager.jda.retrieveUserById(memberArg).complete();
			}
		} catch(Exception e) {
			throw new NoSuchMemberException();
		}

		try {
			Member b = guild.getMemberById(toBan.getId());
			if(!Utils.isSuperiorRank(member, b)) {
				EmbedBuilder builder = Utils.embedWithDescription("You must be a higher rank than this user!").setColor(Color.RED);
				channel.sendMessage(builder.build()).queue();
				return;
			}
		} catch(Exception e) {
			return;
		}

		StringBuffer reasonBuffer = new StringBuffer();

		argsList.forEach(a -> reasonBuffer.append(a).append(" "));

		final String identifier = toBan.getAsTag();

		guild.ban(toBan, 0, reasonBuffer.toString()).queue(success -> {
			StringBuilder reasonBuilder = new StringBuilder(member.getAsMention())
					.append(" banned ").append(identifier);
			if(argsList.size() > 0) {
				reasonBuilder.append(" for reason: ").append(reasonBuffer.toString());
			}


			EmbedBuilder builder = Utils.embedWithDescription(reasonBuilder.toString())
					.setImage(ConfigManager.banGif());
			channel.sendMessage(builder.build()).queue(suc -> message.delete().queueAfter(5, TimeUnit.SECONDS));
		});
	}

	@Override
	public CommandContext getContext() {
		return ConfigManager.getCommandContextByName("administrative", "ban");
	}
}
