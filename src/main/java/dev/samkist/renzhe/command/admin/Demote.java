package dev.samkist.renzhe.command.admin;

import dev.samkist.renzhe.command.lib.Command;
import dev.samkist.renzhe.command.lib.CommandContext;
import dev.samkist.renzhe.command.lib.Evaluate;
import dev.samkist.renzhe.data.NoSuchMemberException;
import dev.samkist.renzhe.utils.ConfigManager;
import dev.samkist.renzhe.utils.Utils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

import java.util.ArrayList;
import java.util.Arrays;

public class Demote implements Command {

	@Override
	public Evaluate<Message, String> getEvaluate() {
		return (message, args) -> {
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

			String memberArg = Utils.parseArgAsId(argsList);


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
		};
	}

	@Override
	public CommandContext getContext() {
		return ConfigManager.getCommandContextByName("administrative", "demote");
	}
}
