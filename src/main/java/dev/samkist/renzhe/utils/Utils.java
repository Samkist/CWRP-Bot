package dev.samkist.renzhe.utils;

import dev.samkist.renzhe.Manager;
import dev.samkist.renzhe.command.lib.CommandContext;
import dev.samkist.renzhe.command.lib.CommandRole;
import dev.samkist.renzhe.command.lib.Evaluate;
import dev.samkist.renzhe.data.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.internal.utils.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static dev.samkist.renzhe.utils.ConfigManager.*;

public class Utils {
	public static final Logger getLogger(Class clazz) {
		return LoggerFactory.getLogger(clazz);
	}

	public static boolean hasStaffPermission(Member member, String permission) {
		if(member.getId().equals(Manager.SAMKIST)) return true;
		return userPermissionIndex(member) >= permissionIndex(permission);
	}

	public static Role getStaffRole(Member member) {
		return getMainGuild().getRoleById(staffRoles().get(userPermissionIndex(member)).getId());
	}

	public static String parseArgAsId(List<String> args) {
		String memberArg = Utils.getIdFromMention(args.get(0));
		args.remove(0);
		return memberArg;
	}

	public static void clearMessage(Message message) {
		message.delete().queueAfter(5, TimeUnit.SECONDS);
	}

	public static String getReason(List<String> args) {
		return String.join(" ", args);
	}

	public static void applyEvaluate(CommandContext context, Message message, String args, Evaluate<Message, String> evaluate) {
		final TextChannel channel = message.getTextChannel();
		try {
			evaluate.accept(message, args);
		} catch(NoSuchRoleException e) {
			sendErrorEmbed(channel,Utils.embedWithDescription(ConfigManager.getNoSuchRoleString()));
		} catch(SelfBenefitException e) {
			sendErrorEmbed(channel, embedWithDescription(ConfigManager.getSelfBenefitString()));
		} catch(SelfInflictionException e) {
			sendErrorEmbed(channel, embedWithDescription(ConfigManager.getSelfInflictedString()));
		} catch (SuperiorRankException e) {
			sendErrorEmbed(channel, embedWithDescription(ConfigManager.getSuperiorRankString()));
		} catch(MainDiscordException e) {
			sendErrorEmbed(channel, embedWithDescription(ConfigManager.getMainDiscordString()));
		} catch(NoPermissionException e) {
			sendErrorEmbed(channel, noPermissionEmbed(context));
		} catch (NoSuchMemberException e) {
			sendErrorEmbed(channel, noSuchMemberEmbed(context));
		} catch (Exception e) {
			e.printStackTrace();
			sendErrorEmbed(channel, ConfigManager.defaultEmbed().setDescription(e.getMessage()));
		}
	}

	public static void sendErrorEmbed(TextChannel channel, EmbedBuilder embed) {
		channel.sendMessage(embed.setColor(Color.RED).build()).queue();
	}

	public static void sendErrorEmbed(TextChannel channel, EmbedBuilder embed, Consumer<Message> success) {
		channel.sendMessage(embed.setColor(Color.RED).build()).queue(success);
	}

	public static void sendEmbed(TextChannel channel, EmbedBuilder embed) {
		channel.sendMessage(embed.build()).queue();
	}

	public static void sendEmbed(TextChannel channel, EmbedBuilder embed, Consumer<Message> success) {
		channel.sendMessage(embed.build()).queue(success);
	}

	public static Boolean isSuperiorRank(Member a, Member b) {
		return userPermissionIndex(a) > userPermissionIndex(b);
	}

	public static Boolean isSuperiorRank(Role a, Role b) {
		return permissionIndex(a) > permissionIndex(b);
	}

	public static String getIdFromMention(String s) {
		return s.replaceAll("[<!@&#>]", "");
	}

	public static Pair<Integer, TimeUnit> parseTime(String timeString) {
		ArrayList<Character> characters = new ArrayList<>();
		for(char c : timeString.toCharArray()) {
			characters.add(c);
		}

		StringBuffer numberBuffer = new StringBuffer();
		StringBuffer unitBuffer = new StringBuffer();
		characters.forEach(c -> {
			if(Character.isDigit(c)) {
				numberBuffer.append(c.toString());
			} else if(Character.isLetter(c)) {
				unitBuffer.append(c.toString());
			}
		});

		//ms, s, m, h, d, w
		Integer delay = Integer.parseInt(numberBuffer.toString());
		TimeUnit unit;

		final String unitString = unitBuffer.toString();

		if(Objects.isNull(unitString)) {
			return Pair.of(delay, TimeUnit.HOURS);
		}

		switch(unitString) {
			case "ms":
				unit = TimeUnit.MILLISECONDS;
				break;
			case "s":
				unit = TimeUnit.SECONDS;
				break;
			case "m":
				unit = TimeUnit.MINUTES;
				break;
			case "d":
				unit = TimeUnit.DAYS;
				break;
			case "w":
				unit = TimeUnit.DAYS;
				delay *= 7;
				break;
			default:
				unit = TimeUnit.HOURS;
		}


		return Pair.of(delay, unit);

	}

	public static void lockdownChannel(TextChannel channel, long expires) {
		final Guild guild = channel.getGuild();

		long now = Instant.now().toEpochMilli();
		long difference = expires - now;

		// code to add lock overrides


		final LockdownData data = new LockdownData(channel.getId(), expires);
		DBManager.saveLockdown(data);

		// timer to revoke previously added overrides
	}

	public static void muteMember(Member member, long expires, String reason, String staffId) {
		final Guild guild = getMainGuild();
		final Role muteRole = getMuteRole();

		long now = Instant.now().toEpochMilli();
		long difference = expires - now;

		guild.addRoleToMember(member, muteRole).queue();

		final MuteData data = new MuteData(member.getId(), reason, staffId, expires);

		DataManager.saveMute(data);
		guild.removeRoleFromMember(member, muteRole).queueAfter(difference, TimeUnit.MILLISECONDS);
	}

	public static Role getMuteRole() {
		return getMainGuild().getRoleById(ConfigManager.getMuteId());
	}

	public static Role getNextStaffRole(Member member) {
		List<Role> staffRoles = staffRoles();
		Role staffRole = getStaffRole(member);
		Integer userPermissionIndex = userPermissionIndex(member);
		if(userPermissionIndex + 1 < staffRoles.size()) {
			return staffRoles.get(userPermissionIndex + 1);
		} else {
			return staffRole;
		}
	}

	public static Role getPreviousStaffRole(Member member) {
		Role staffRole = getStaffRole(member);
		Integer userPermissionIndex = userPermissionIndex(member);
		if(userPermissionIndex - 1 >= 0) {
			return staffRoles().get(userPermissionIndex - 1);
		} else {
			return staffRole;
		}
	}

	public static Integer userPermissionIndex(Member member) {
		List<Role> memberRoles = member.getRoles();
		List<String> ownedPermissions = fetchStaffCommandRoles().stream().filter(commandRole ->
				memberRoles.stream().map(Role::getId)
						.collect(Collectors.toList()).contains(commandRole.getId())).map(CommandRole::getName).collect(Collectors.toList());
		String highestPermission = ownedPermissions.get(ownedPermissions.size()-1);
		List<String> staffRolesByName = fetchStaffCommandRoles().stream().map(CommandRole::getName).collect(Collectors.toList());
		int index = -1;
		for(int i = 0; i < staffRolesByName.size(); i++) {
			System.out.println("Checking " + staffRolesByName.get(i) + " against " + highestPermission);
			if(staffRolesByName.get(i).equalsIgnoreCase(highestPermission)) {
				index = i;
				break;
			}
		}
		return index;
	}

	public static List<Role> staffRoles() {
		return fetchStaffCommandRoles().stream().map(cr -> getMainGuild().getRoleById(cr.getId())).collect(Collectors.toList());
	}

	public static Integer permissionIndex(String permission) {
		List<String> staffRolesByName = fetchStaffCommandRoles().stream().map(CommandRole::getName).collect(Collectors.toList());
		return staffRolesByName.indexOf(permission);
	}

	public static Integer permissionIndex(CommandContext context) {
		return permissionIndex(context.requiredPermission());
	}

	public static Integer permissionIndex(Role role) {
		return staffRoles().indexOf(role);
	}

	public static boolean hasStaffPermission(Member member, CommandContext context) {
		return hasStaffPermission(member, context.requiredPermission());
	}

	public static Role roleByPermission(String permission) {
		String roleId = fetchStaffCommandRoles().stream().filter(r ->
				r.getName().equalsIgnoreCase(permission)).findFirst().orElse(null).getId();
		return getMainGuild().getRoleById(roleId);
	}

	public static Role roleByContext(CommandContext context) {
		return roleByPermission(context.requiredPermission());
	}

	public static boolean canUseRole(Member member, Role targetRole) {
		if(member.getId().equals(Manager.SAMKIST)) return true;
		String rolePermission = ctx.read("$.categories.administrative.role.permission");
		Integer highestRoleIndex = member.getRoles().get(0).getPosition();
		Integer appliedRoleIndex = targetRole.getPosition();
		if(appliedRoleIndex >= highestRoleIndex) {
			return false;
		}
		if(hasStaffPermission(member, rolePermission)) {
			return true;
		}
		List<DepartmentContext> departmentContexts = fetchDepartmentContexts();
		boolean needsCommand = departmentContexts.stream().anyMatch(department -> {
			String toRoleId = targetRole.getId();
			boolean addingSupervisor = toRoleId.equals(department.getSupervisor());
			boolean addingInterviewer = toRoleId.equals(department.getInvterviewer());
			return addingSupervisor || addingInterviewer;
		});
		boolean isCommand = departmentContexts.stream().anyMatch(department ->
				member.getRoles().stream().map(Role::getId).anyMatch(r -> r.equals(department.getCommand())));
		if(needsCommand) {
			return isCommand;
		}

		boolean needsInterviewer = departmentContexts.stream().anyMatch(department -> {
			String toRoleId = targetRole.getId();
			boolean addingDepartment = toRoleId.equals(department.getDepartment());
			boolean addingInterview = toRoleId.equals(department.getInterview());
			boolean addingLEO = toRoleId.equals(leoRole);
			return addingDepartment || addingInterview || addingLEO;
		});

		boolean isInterviewer = departmentContexts.stream().anyMatch(department ->
				member.getRoles().stream().map(Role::getId).anyMatch(r -> r.equals(department.getInvterviewer())));
		if(needsInterviewer) {
			return isCommand || isInterviewer;
		}
		return false;
	}

	public static EmbedBuilder embedWithDescription(String description) {
		return defaultEmbed().setDescription(description);
	}

	public static EmbedBuilder noPermissionEmbed(CommandContext context) {
		Role r = roleByPermission(context.requiredPermission());
		String mention = r.getAsMention();
		EmbedBuilder embed = ConfigManager.defaultEmbed()
				.addField("**No Permission**", "Must be equal to or higher in rank than " + mention,true)
				.setColor(Color.red);

		return embed;
	}

	public static EmbedBuilder noSuchMemberEmbed(CommandContext context) {
		EmbedBuilder embed = ConfigManager.defaultEmbed()
				.setColor(Color.red)
				.addField("**Error**", "`Invalid Player`", true)
				.addField("**Usage**", "`" + context.usage() + "`", true);
		return embed;
	}
}
