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
	protected static ReadContext ctx;
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
	}

	public static String getPrefix() {
		return ctx.read("$.config.prefix");
	}

	public static String getToken() {
		return ctx.read("$.config.token");
	}

	public static Guild getMainGuild() {
		return jda.getGuildById(ctx.read("$.config.guild"));
	}

	public static String getLeoRole() {
		return leoRole;
	}

	public static String getPermissionByCommandName(String command) {
		return ctx.read("$.categories.administrative." + command + ".permission");
	}

	public static String getDescriptionByCommandName(String command) {
		return ctx.read("$.categories.administrative." + command + ".description");
	}

	public static List<String> getCommandTriggers(String command) {
		return ctx.read("$.categories.administrative." + command + ".trigger[*]", List.class);
	}

	public static CommandContext getCommandContextByName(String category, String command) {
		final String name = command;
		final String description = ctx.read("$.categories." + category + ".commands." + command + ".description");
		final String usage = ctx.read("$.categories." + category + ".commands." + command + ".usage");
		final String requiredPermission = ctx.read("$.categories." + category + ".commands." + command + ".permission");
		final List<String> triggers = ctx.read("$.categories." + category + ".commands." + command + ".trigger[*]", List.class);
		final List<CommandContext.Attribute> attributes = new ArrayList<>();
		final Optional<String> optionalColor = Optional.ofNullable(ctx.read("$.categories." + category + ".commands." + command + ".color"));
		final Optional<Boolean> visibility = Optional.ofNullable(ctx.read("$.categories." + category + ".commands." + command + ".visible", boolean.class));
		AtomicReference<Color> color = new AtomicReference<>();
		optionalColor.ifPresentOrElse(hex -> color.set(Color.decode(hex)), () -> color.set(loadColors().get("default")));
		return new CommandContext(name, description, requiredPermission, usage, triggers, attributes, color.get());
	}

	public static List<DepartmentContext> loadDepartmentContexts() {
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

	public static List<CommandRole> loadStaffCommandRoles() {
		Iterator<String> names = ctx.read("$.roles.staff[*].name", List.class).iterator();
		Iterator<String> ids = ctx.read("$.roles.staff[*].id", List.class).iterator();
		List<CommandRole> permissions = new ArrayList<>();
		while(names.hasNext() && ids.hasNext()) {
			permissions.add(new CommandRole(names.next(), ids.next()));
		}
		return permissions;
	}

	public static Map<String, Color> loadColors() {
		Map<String, String> rawColors = ctx.read("$.config.colors", Map.class);
		Map<String, Color> colors = new HashMap<>();

		rawColors.forEach((name, color) -> colors.put(name, Color.decode(color)));

		return colors;
	}

	public static Map<String, TextChannel> loadTextChannels() {
		Map<String, TextChannel> channelsByName = new HashMap<>();
		Guild main = getMainGuild();
		Map<String, String> rawChannels = ctx.read("$.config.channels", Map.class);
		rawChannels.forEach((name, id) -> channelsByName.put(name, main.getTextChannelById(id)));
		return channelsByName;
	}

	public static String getEmbedAuthor() {
		return ctx.read("$.config.embed.author");
	}

	public static String getEmbedFooter() {
		return ctx.read("$.config.embed.footer");
	}

	public static EmbedBuilder defaultEmbed() {
		EmbedBuilder builder = new EmbedBuilder();
		String pfp = jda.getSelfUser().getAvatarUrl();
		builder.setAuthor(getEmbedAuthor(), pfp, pfp)
				.setColor(loadColors().get("default"))
				.setFooter(getEmbedFooter())
				.setTimestamp(Instant.now());

		return builder;
	}

	public static EmbedBuilder defaultEmbedT() {
		return defaultEmbed()
				.setThumbnail(jda.getSelfUser().getAvatarUrl());
	}

	public static TextChannel getChannelByFriendlyName(String friendlyName) {
		return loadTextChannels().get(friendlyName);
	}

	public static String banGif() {
		return ctx.read("$.config.ban-gif");
	}
}
