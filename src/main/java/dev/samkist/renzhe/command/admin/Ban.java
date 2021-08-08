package dev.samkist.renzhe.command.admin;

import dev.samkist.renzhe.Manager;
import dev.samkist.renzhe.command.lib.Command;
import dev.samkist.renzhe.command.lib.CommandContext;
import dev.samkist.renzhe.command.lib.Evaluate;
import dev.samkist.renzhe.data.NoPermissionException;
import dev.samkist.renzhe.data.NoSuchMemberException;
import dev.samkist.renzhe.data.SelfInflictionException;
import dev.samkist.renzhe.data.SuperiorRankException;
import dev.samkist.renzhe.utils.ConfigManager;
import dev.samkist.renzhe.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;

import java.util.ArrayList;
import java.util.Arrays;

public class Ban implements Command {

	@Override
	public Evaluate<Message, String> getEvaluate() {
		return (message, args) -> {
			final Member member = message.getMember();
			final TextChannel channel = message.getTextChannel();
			final Guild guild = member.getGuild();
			final ArrayList<String> argsList = new ArrayList(Arrays.asList(args.split(" ")));
			if(!Utils.hasStaffPermission(member, getContext())) {
				throw new NoPermissionException();
			}

			if(argsList.size() == 0) {
				throw new NoSuchMemberException();
			}


			String memberArg = Utils.parseArgAsId(argsList);

			User toBan = null;
			try {
				toBan = Manager.jda.retrieveUserById(memberArg).complete();
			} catch(Exception e) {
				throw new NoSuchMemberException();
			}

			try {
				Member b = guild.getMemberById(toBan.getId());
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

			guild.ban(toBan, 0, reason).queue(success -> {
				StringBuilder reasonBuilder = new StringBuilder(member.getAsMention())
						.append(" banned ").append(identifier);
				if(argsList.size() > 0) {
					reasonBuilder.append(" for reason: ").append(reason);
				}


				EmbedBuilder builder = Utils.embedWithDescription(reasonBuilder.toString())
						.setImage(ConfigManager.banGif());
				Utils.sendEmbed(channel, builder, (suc -> Utils.clearMessage(message)));
			});
		};
	}

	@Override
	public CommandContext getContext() {
		return ConfigManager.getCommandContextByName("administrative", "ban");
	}
}
