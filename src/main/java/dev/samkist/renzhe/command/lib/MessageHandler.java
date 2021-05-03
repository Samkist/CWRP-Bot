package dev.samkist.renzhe.command.lib;

import dev.samkist.renzhe.utils.ConfigManager;
import dev.samkist.renzhe.utils.Emoji;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.internal.utils.PermissionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MessageHandler extends ListenerAdapter {
	public static final CommandHandler commandHandler = new CommandHandler();
	private static final Logger log = LoggerFactory.getLogger(MessageHandler.class);
	private static final ThreadGroup threadGroup = new ThreadGroup("Command Executor - Group");
	private static final Executor commandsExecutor = Executors.newCachedThreadPool(r -> new Thread(threadGroup, r, "Command Pool"));
	private final String prefix;

	static {
		// Set up the thread group to have a lower priority.
		threadGroup.setMaxPriority(Thread.NORM_PRIORITY - 1);
		commandHandler.registerCommands(new CommandRegistry().getCommands());
	}

	public MessageHandler() {
		this.prefix = ConfigManager.getPrefix();
	}

	@Override
	public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
		if(event.getAuthor().isBot())
			return;

		if(!Objects.isNull(event.getGuild())) {
			if (event.getMessage().getContentRaw().replace(this.prefix, "").equalsIgnoreCase(event.getGuild().getSelfMember().getAsMention().replace(this.prefix, ""))) {
				if (RateLimiter.checkIfRateLimited(event.getAuthor())) return;
				event.getChannel().sendMessage("**My prefix here is `" + "TODO" + "`**").queue();
				return;
			}
		}

		commandsExecutor.execute(() -> {
			String prefix = this.prefix;
			String message = event.getMessage().getContentRaw();
			if (!message.startsWith(prefix)) return;
			String[] splitMessage = message.split("\\s+", 2);
			String commandString;
			try {
				commandString = splitMessage[0].substring(prefix.length());
			} catch (Exception e) {
				return;
			}

			Command command = commandHandler.findCommand(commandString.toLowerCase());
			if (command == null) return;
			if (RateLimiter.checkIfRateLimited(event.getAuthor())) return;
			boolean isDM = event.isFromType(ChannelType.PRIVATE);
			if (isDM && !command.hasAttribute("privateMessages")) {
				event.getChannel().sendMessage(Emoji.WARNING + " **This command cannot be used in private messages**").queue();
				return;
			}
			if (!isDM) {
				if (!PermissionUtil.checkPermission(event.getTextChannel(), event.getGuild().getSelfMember(), Permission.MESSAGE_WRITE))
					return;
			}
			if (event.getGuild() != null) {
				boolean hasPerms = PermissionUtil.checkPermission(event.getTextChannel(), event.getGuild().getSelfMember(), Permission.MESSAGE_EMBED_LINKS);
				if (!hasPerms) {
					event.getChannel().sendMessage(Emoji.NO_ENTRY + " **I can not execute any commands because I do not have permission to use embeds. [Embed Links]**").queue();
					return;
				}
			}


			MessageChannel channel = event.getChannel();
			try {
				commandHandler.execute(command, event.getMessage(), splitMessage.length > 1 ? splitMessage[1] : "");
			} catch (Exception e) {
				log.error("Encountered error while executing command " + command.getContext().name(), e);
				channel.sendMessage(":x: **I failed to execute that command**, encountered: `" + e.getClass().getCanonicalName() + (e.getMessage() != null ? " " + e.getMessage() : "") + "`").queue();
			}
		});
	}
}
