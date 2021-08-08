package dev.samkist.renzhe.command.admin;

import dev.samkist.renzhe.command.lib.Command;
import dev.samkist.renzhe.command.lib.CommandContext;
import dev.samkist.renzhe.command.lib.Evaluate;
import dev.samkist.renzhe.data.NoPermissionException;
import dev.samkist.renzhe.data.NoSuchMemberException;
import dev.samkist.renzhe.data.SelfBenefitException;
import dev.samkist.renzhe.data.SuperiorRankException;
import dev.samkist.renzhe.utils.ConfigManager;
import dev.samkist.renzhe.utils.Utils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class Hire implements Command {


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

			Member toPromote = null;
			try {
				toPromote = guild.getMemberById(memberArg);
				if(!Utils.isSuperiorRank(member, toPromote)) {
					throw new SuperiorRankException();
				}
			} catch(SuperiorRankException e) {
				throw new SuperiorRankException();
			} catch(Exception e) {

			}

			if(toPromote.getId().equals(member.getId())) {
				throw new SelfBenefitException();
			}

			Role currentRole = Utils.getStaffRole(member);

			Role role = null;

			try {
				role = guild.getRoleById(Utils.parseArgAsId(argsList));
			} catch(Exception e) {
				role = Utils.getNextStaffRole(toPromote);
			}

			if(!Utils.isSuperiorRank(Utils.getStaffRole(member), role)) {
				throw new SuperiorRankException();
			}

			final String mention = toPromote.getAsMention();
			final String msg = " has been hired as " + role.getAsMention();
			final String string = mention + msg + " by " + member.getAsMention();
			final TextChannel staffMovement = ConfigManager.getChannelByFriendlyName("staff-movement");
			guild.addRoleToMember(member, role).queue(success ->
					Utils.sendEmbed(staffMovement, Utils.embedWithDescription(string)));


			if(Objects.nonNull(currentRole)) {
				guild.removeRoleFromMember(member, currentRole).queue();
			}
		};


	}

	@Override
	public CommandContext getContext() {
		return ConfigManager.getCommandContextByName("administrative", "hire");
	}
}
