package dev.samkist.renzhe.command.lib;

import dev.samkist.renzhe.command.user.*;
import dev.samkist.renzhe.command.admin.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CommandRegistry {
	private static final Set<Command> commands = new HashSet<>();

	CommandRegistry() {
		//Public
		register(
				new Avatar(),
				new Say(),
				new RoleCommands(),
				new Say(),
				new Suggest()
		);
		//Admin
		register(
			new Ban(),
			new Demote(),
			new Terminate(),
			new GlobalBan(),
			new Hire(),
			new Interview(),
			new Kick(),
			new Lockdown(),
			new Mute(),
			new Nuke(),
			new Promote(),
			new Purge(),
			new Resign(),
			new Role(),
			new Unban(),
			new Unmute(),
				new SpamSomeone(),
				new CopyColor()
		);
	}

	private void register(Command... cmds) {
		commands.addAll(Arrays.asList(cmds));
	}

	Set<Command> getCommands() {
		return commands;
	}
}
