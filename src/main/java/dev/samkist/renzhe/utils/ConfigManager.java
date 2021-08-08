package dev.samkist.renzhe.utils;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.ReadContext;
import dev.samkist.renzhe.command.lib.CommandContext;
import dev.samkist.renzhe.command.lib.CommandRole;
import dev.samkist.renzhe.data.DepartmentContext;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.time.Instant;
import java.util.List;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static dev.samkist.renzhe.Manager.jda;

public final class ConfigManager {

	private static final AtomicReference<File> configFile = new AtomicReference<>();
	private static final AtomicReference<String> json = new AtomicReference<>();
	private static ArrayList<CommandContext> commandContexts = new ArrayList<>();
	private static List<DepartmentContext> departmentContexts;
	private static List<CommandRole> staffCommandRoles;
	private static Map<String, Color> colors;
	private static Map<String, TextChannel> textChannels;
	private static String embedFooter;
	private static String embedAuthor;
	private static String prefix;
	private static String mainGuildId;
	private static String s_SelfInflicted ;
	private static String s_SelfBenefit;
	private static String s_SuperiorRank;
	private static String s_MainDiscord;
	private static String s_NoSuchRole;
	protected static ReadContext ctx;
	private static String muteRole;
	protected static String leoRole;

	public static void initialize() throws Exception {
		configFile.set(new File("configuration.json"));
		BufferedReader reader = new BufferedReader(new FileReader(configFile.get()));
		StringBuilder stringBuilder = new StringBuilder();
		String line;
		String ls = System.getProperty("line.separator");
		while ((line = reader.readLine()) != null) {
			stringBuilder.append(line);
			stringBuilder.append(ls);
		}
		// delete the last new line separator
		stringBuilder.deleteCharAt(stringBuilder.length() - 1);
		reader.close();

		json.set(stringBuilder.toString());
		Configuration config = Configuration.defaultConfiguration().addOptions(Option.DEFAULT_PATH_LEAF_TO_NULL);
		ctx = JsonPath.using(config).parse(json.get());
		leoRole = ctx.read("$.roles.leo.leo-role");
		muteRole = ctx.read("$.config.mute-role");
	}

	public static void reloadCache() throws Exception {
		initialize();
		departmentContexts = loadDepartmentContexts();
		staffCommandRoles = loadStaffCommandRoles();
		colors = loadColors();
		textChannels = loadTextChannels();
		embedFooter = loadEmbedFooter();
		embedAuthor = loadEmbedAuthor();
		prefix = loadPrefix();
		mainGuildId = loadMainGuildId();
		s_SelfInflicted = loadSelfInflictedString();
		s_SelfBenefit = loadSelfBenefitString();
		s_SuperiorRank = loadSuperiorRankString();
		s_MainDiscord = loadMainDiscordString();
		s_NoSuchRole = loadNoSuchRoleString();
	}

	public static String getMuteId() {
		return muteRole;
	}

	public static String getConnectionString() {
		return ctx.read("$.config.database.connection-string");
	}

	public static String getDatabase() {
		return ctx.read("$.config.database.database");
	}

	public static String getNoSuchRoleString() {
		if(Objects.isNull(s_NoSuchRole)) {
			s_NoSuchRole = loadNoSuchRoleString();
		}

		return s_NoSuchRole;
	}

	private static String loadNoSuchRoleString() {
		return ctx.read("$.config.strings.no-such-role");
	}

	public static String getSelfInflictedString() {
		if(Objects.isNull(s_SelfInflicted)) {
			s_SelfInflicted = loadSelfInflictedString();
		}

		return s_SelfInflicted;
	}

	private static String loadSelfInflictedString() {
		return ctx.read("$.config.strings.self-inflicted");
	}

	public static String getSelfBenefitString() {
		if(Objects.isNull(s_SelfBenefit)) {
			s_SelfBenefit = loadSelfBenefitString();
		}

		return s_SelfBenefit;
	}

	private static String loadSelfBenefitString() {
		return ctx.read("$.config.strings.self-inflicted");
	}

	public static String getSuperiorRankString() {
		if(Objects.isNull(s_SuperiorRank)) {
			s_SuperiorRank = loadSuperiorRankString();
		}

		return s_SuperiorRank;
	}

	private static String loadSuperiorRankString() {
		return ctx.read("$.config.strings.self-inflicted");
	}

	public static String getMainDiscordString() {
		if(Objects.isNull(s_MainDiscord)) {
			s_MainDiscord = loadMainDiscordString();
		}

		return s_MainDiscord;
	}

	private static String loadMainDiscordString() {
		return ctx.read("$.config.strings.self-inflicted");
	}

	public static String getPrefix() {
		if(Objects.isNull(prefix)) {
			prefix = loadPrefix();
		}

		return prefix;
	}

	private static String loadPrefix() {
		return ctx.read("$.config.prefix");
	}

	public static String getToken() {
		return ctx.read("$.config.token");
	}

	public static String getMainGuildId() {
		if(Objects.isNull(mainGuildId)) {
			mainGuildId = loadMainGuildId();
		}

		return mainGuildId;
	}

	private static String loadMainGuildId() {
		return ctx.read("$.config.guild");
	}

	public static Guild getMainGuild() {
		return jda.getGuildById(getMainGuildId());
	}

	public static String getLeoRole() {
		return leoRole;
	}

	public static CommandContext getCommandContextByName(String category, String command) {
		Optional<CommandContext> context = findCommandContextByName(category, command);
		if(context.isEmpty()) {
			commandContexts.add(loadCommandContextByName(category, command));
			return getCommandContextByName(category, command);
		} else {
			return context.get();
		}
	}

	private static Optional<CommandContext> findCommandContextByName(String category, String command) {
		return commandContexts.stream().filter(context -> context.category().equalsIgnoreCase(category) && context.name().equalsIgnoreCase(command)).findFirst();
	}

	private static CommandContext loadCommandContextByName(String category, String command) {
		final String name = command;
		final String description = ctx.read("$.categories." + category + ".commands." + command + ".description");
		final String usage = ctx.read("$.categories." + category + ".commands." + command + ".usage");
		final String requiredPermission = ctx.read("$.categories." + category + ".commands." + command + ".permission");
		final List<String> triggers = ctx.read("$.categories." + category + ".commands." + command + ".trigger[*]", List.class);
		final List<CommandContext.Attribute> attributes = new ArrayList<>();
		final Optional<String> optionalColor = Optional.ofNullable(ctx.read("$.categories." + category + ".commands." + command + ".color"));
		final Optional<Boolean> visibility = Optional.ofNullable(ctx.read("$.categories." + category + ".commands." + command + ".visible", boolean.class));
		AtomicReference<Color> color = new AtomicReference<>();
		optionalColor.ifPresentOrElse(hex -> color.set(Color.decode(hex)), () -> color.set(fetchColors().get("default")));
		return new CommandContext(category, name, description, requiredPermission, usage, triggers, attributes, color.get()).visible(visibility.orElse(true));
	}

	public static List<DepartmentContext> fetchDepartmentContexts() {
		if(Objects.isNull(departmentContexts)) {
			departmentContexts = loadDepartmentContexts();
		}
		return departmentContexts;
	}

	private static List<DepartmentContext> loadDepartmentContexts() {
		ArrayList<DepartmentContext> departmentContexts = new ArrayList<>();
		List<Map<String, String>> context = ctx.read("$.roles.leo.ids-to-roles[*]", List.class);
		context.forEach(department -> {
			String name = department.get("name");
			String command = department.get("command");
			String supervisor = department.get("supervisor");
			String interviewer = department.get("interviewer");
			String departmentRole = department.get("department");
			String interview = department.get("interview");
			DepartmentContext dptContext = new DepartmentContext(name, command, supervisor, interviewer, departmentRole, interview);
			departmentContexts.add(dptContext);
		});
		return departmentContexts;
	}

	public static List<CommandRole> fetchStaffCommandRoles() {
		if(Objects.isNull(staffCommandRoles)) {
			staffCommandRoles = loadStaffCommandRoles();
		}
		return staffCommandRoles;
	}

	private static List<CommandRole> loadStaffCommandRoles() {
		Iterator<String> names = ctx.read("$.roles.staff[*].name", List.class).iterator();
		Iterator<String> ids = ctx.read("$.roles.staff[*].id", List.class).iterator();
		List<CommandRole> permissions = new ArrayList<>();
		while(names.hasNext() && ids.hasNext()) {
			permissions.add(new CommandRole(names.next(), ids.next()));
		}
		return permissions;
	}

	private static Map<String, Color> fetchColors() {
		if(Objects.isNull(colors)) {
			colors = loadColors();
		}

		return colors;
	}

	private static Map<String, Color> loadColors() {
		Map<String, String> rawColors = ctx.read("$.config.colors", Map.class);
		Map<String, Color> colors = new HashMap<>();

		rawColors.forEach((name, color) -> colors.put(name, Color.decode(color)));

		return colors;
	}

	public static Map<String, TextChannel> fetchTextChannels() {
		if(Objects.isNull(textChannels)) {
			textChannels = loadTextChannels();
		}

		return textChannels;
	}

	private static Map<String, TextChannel> loadTextChannels() {
		Map<String, TextChannel> channelsByName = new HashMap<>();
		Guild main = getMainGuild();
		Map<String, String> rawChannels = ctx.read("$.config.channels", Map.class);
		rawChannels.forEach((name, id) -> channelsByName.put(name, main.getTextChannelById(id)));
		return channelsByName;
	}

	public static String fetchEmbedAuthor() {
		if(Objects.isNull(embedAuthor)) {
			embedAuthor = loadEmbedAuthor();
		}

		return embedAuthor;
	}

	private static String loadEmbedAuthor() {
		return ctx.read("$.config.embed.author");
	}

	public static String fetchEmbedFooter() {
		if(Objects.isNull(embedFooter)) {
			embedFooter = loadEmbedFooter();
		}

		return embedFooter;
	}

	private static String loadEmbedFooter() {
		return ctx.read("$.config.embed.footer");
	}

	public static EmbedBuilder defaultEmbed() {
		EmbedBuilder builder = new EmbedBuilder();
		String pfp = jda.getSelfUser().getAvatarUrl();
		builder.setAuthor(fetchEmbedAuthor(), pfp, pfp)
				.setColor(fetchColors().get("default"))
				.setFooter(fetchEmbedFooter())
				.setTimestamp(Instant.now());

		return builder;
	}

	public static EmbedBuilder defaultEmbedT() {
		return defaultEmbed()
				.setThumbnail(jda.getSelfUser().getAvatarUrl());
	}

	public static TextChannel getChannelByFriendlyName(String friendlyName) {
		return fetchTextChannels().get(friendlyName);
	}

	public static String banGif() {
		return ctx.read("$.config.ban-gif");
	}
}
