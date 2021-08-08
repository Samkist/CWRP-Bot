package dev.samkist.renzhe.command.admin;

import dev.samkist.renzhe.command.lib.Command;
import dev.samkist.renzhe.command.lib.CommandContext;
import dev.samkist.renzhe.command.lib.Evaluate;
import dev.samkist.renzhe.data.NoPermissionException;
import dev.samkist.renzhe.data.NoSuchRoleException;
import dev.samkist.renzhe.utils.ConfigManager;
import dev.samkist.renzhe.utils.Utils;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.managers.RoleManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class CopyColor implements Command {
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

			if(argsList.size() == 1) {
				throw new Exception("Please mention two roles");
			}

			final String roleOneArg = Utils.parseArgAsId(argsList);
			final String roleTwoArg = Utils.parseArgAsId(argsList);

			Role roleOne;
			Role roleTwo;

			try {
				roleOne = guild.getRoleById(roleOneArg);
				roleTwo = guild.getRoleById(roleTwoArg);
				if(Objects.isNull(roleOne) || Objects.isNull(roleTwo)) {
					throw new Exception();
				}
			} catch(Exception e) {
				throw new NoSuchRoleException();
			}

			RoleManager roleManager = roleTwo.getManager();

			roleManager.setColor(roleOne.getColor()).queue();

			Utils.sendEmbed(channel, Utils.embedWithDescription("Successfully " + roleOne.getAsMention() + " -> " + roleTwo.getAsMention()));

		};
	}

	@Override
	public CommandContext getContext() {
		return ConfigManager.getCommandContextByName("administrative", "colorcopy");
	}
}
