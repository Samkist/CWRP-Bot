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
import net.dv8tion.jda.internal.utils.tuple.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class Lockdown implements Command {
	@Override
	public Evaluate<Message, String> getEvaluate() {
		return (message, args) -> {
			final Member member = message.getMember();
			final TextChannel channel = message.getTextChannel();
			final ArrayList<String> argsList = new ArrayList(Arrays.asList(args.split(" ")));
			if(!Utils.hasStaffPermission(member, getContext())) {
				throw new NoPermissionException();
			}

			final String channelArg = Utils.parseArgAsId(argsList);

			final TextChannel toLockdown;
			try {
				toLockdown = member.getGuild().getTextChannelById(channelArg);
			} catch(Exception e) {
				throw new Exception("No such channel");
			}


			Pair<Integer, TimeUnit> timeToLockdown = Utils.parseTime(argsList.get(0));

			long expiry = timeToLockdown.getRight().toMillis(timeToLockdown.getLeft());



		};
	}

	@Override
	public CommandContext getContext() {
		return ConfigManager.getCommandContextByName("administrative", "lockdown");
	}
}
