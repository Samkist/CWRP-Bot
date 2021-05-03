package dev.samkist.renzhe;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import dev.samkist.renzhe.command.lib.MessageHandler;
import dev.samkist.renzhe.utils.ConfigManager;
import dev.samkist.renzhe.utils.Utils;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class Manager implements EventListener {
	public static final AtomicReference<String> ref = new AtomicReference<>("cringe");
	private static final Logger log = Utils.getLogger(Manager.class);
	public static final String SAMKIST = "133231388538306560";
	public static final Cache<String, List<Invite>> GuildInviteCache = CacheBuilder.newBuilder().concurrencyLevel(10).maximumSize(10000).build();
	public static EventWaiter eventWaiter = new EventWaiter();
	public static JDA jda;
	private static Object[] eventListeners;

	public static void main(String[] args) {
		System.setProperty("http.agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.167 Safari/537.36");
		try {
			//LOAD CONFIG
			ConfigManager.initialize();
		} catch (Exception e) {
			log.error("Encountered exception while initiating ConfigurationBuilder", e);
			System.exit(1);
		}
		eventListeners = new Object[]{
				new MessageHandler(), eventWaiter,
		};
		// Start JDA
		login();
	}

	public static void login() {
		try {
			jda = JDABuilder.createDefault(ConfigManager.getToken())
					.addEventListeners(eventListeners)
					.enableIntents(GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_MEMBERS)
					.enableCache(CacheFlag.ACTIVITY, CacheFlag.CLIENT_STATUS)
					.setMemberCachePolicy(MemberCachePolicy.ALL)
					.build();
			jda.awaitReady();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		jda.getGuilds().forEach(g -> GuildInviteCache.put(g.getId(), g.retrieveInvites().complete()));
	}

	@Override
	public void onEvent(GenericEvent event)
	{
		if (event instanceof ReadyEvent)
			log.info("API Ready!");
	}

}
