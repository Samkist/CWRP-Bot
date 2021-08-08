package dev.samkist.renzhe.command.admin;

import dev.samkist.renzhe.Manager;
import dev.samkist.renzhe.command.lib.Command;
import dev.samkist.renzhe.command.lib.CommandContext;
import dev.samkist.renzhe.command.lib.Evaluate;
import dev.samkist.renzhe.data.*;
import dev.samkist.renzhe.utils.ConfigManager;
import dev.samkist.renzhe.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class GlobalBan implements Command {
	@Override
	public Evaluate<Message, String> getEvaluate() {
		return (message, args) -> {
			final Member member = message.getMember();
			final TextChannel channel = message.getTextChannel();
			final ArrayList<String> argsList = new ArrayList(Arrays.asList(args.split(" ")));
			if(!Utils.hasStaffPermission(member, getContext())) {
				throw new NoPermissionException();
			}

			if(!message.getGuild().getId().equals(ConfigManager.getMainGuild().getId())) {
				throw new MainDiscordException();
			}

			if(argsList.size() == 1) {
				throw new NoSuchMemberException();
			}


			final String memberArg = Utils.parseArgAsId(argsList);

			User toBan = null;
			try {
				toBan = Manager.jda.retrieveUserById(memberArg).complete();
			} catch(Exception e) {
				throw new NoSuchMemberException();
			}

			try {
				Member b = ConfigManager.getMainGuild().getMemberById(toBan.getId());
				if(!Utils.isSuperiorRank(member, b)) {
					throw new SuperiorRankException();
				}
			} catch(SuperiorRankException e) {
				throw new SuperiorRankException();
			} catch(Exception e) {

			}

			if(toBan.getId().equals(member.getId())) {
				throw new SelfInflictionException();
			}

			String reason = Utils.getReason(argsList);

			final String identifier = toBan.getAsTag();
			final User toBanUser = toBan;

			try {
				List<Guild> guilds = Manager.jda.getGuilds();
				guilds.forEach(guild -> guild.ban(toBanUser, 0, reason).complete());
			} catch (Exception e) {
				throw new Exception("AHHHHH");
			}

			StringBuilder reasonBuilder = new StringBuilder(member.getAsMention())
					.append(" global banned ").append(identifier);
			if(argsList.size() > 0) {
				reasonBuilder.append(" for reason: ").append(reason);
			}

			EmbedBuilder builder = Utils.embedWithDescription(reasonBuilder.toString())
					.setImage(ConfigManager.banGif());
			channel.sendMessage(builder.build()).queue(suc -> message.delete().queueAfter(5, TimeUnit.SECONDS));
		};
	}

	@Override
	public CommandContext getContext() {
		return ConfigManager.getCommandContextByName("administrative", "globalban");
	}
}
