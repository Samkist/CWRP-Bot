package dev.samkist.renzhe.command.admin;

import dev.samkist.renzhe.utils.ConfigManager;
import dev.samkist.renzhe.Manager;
import dev.samkist.renzhe.command.lib.Command;
import dev.samkist.renzhe.command.lib.CommandContext;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SpamSomeone implements Command {
	/**
	 * This is the method called on to execute the command.
	 *
	 * @param message The message which triggered the command.
	 * @param args    The arguments of the commands.
	 * @since 1.0.0
	 */
	@Override
	public void execute(Message message, String args) {
		if(!message.getMember().getId().equals(Manager.SAMKIST)) return;
		try {
			Member member = message.getGuild().getMemberById(args);
			URL url = null;

			url = new URL("https://gist.githubusercontent.com/MattIPv4/045239bc27b16b2bcf7a3a9a4648c08a/raw/2411e31293a35f3e565f61e7490a806d4720ea7e/bee%2520movie%2520script");

			BufferedReader reader = new BufferedReader(
					new InputStreamReader(url.openStream()));
			StringBuffer stringBuffer = new StringBuffer();
			String inputLine;
			while ((inputLine = reader.readLine()) != null) {
				stringBuffer.append(inputLine);
			}

			member.getUser().openPrivateChannel().queue(s -> splitToNChar(stringBuffer.toString(), 1500).forEach(m -> s.sendMessage(m).queue()));

		} catch(Exception e) {

		}

	}

	@Override
	public CommandContext getContext() {
		return ConfigManager.getCommandContextByName("administrative", "spam").visible(false);
	}

	private static List<String> splitToNChar(String text, int size) {
		List<String> parts = new ArrayList<>();

		int length = text.length();
		for (int i = 0; i < length; i += size) {
			parts.add(text.substring(i, Math.min(length, i + size)));
		}
		return parts;
	}
}
