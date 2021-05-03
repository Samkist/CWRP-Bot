package dev.samkist.renzhe.utils;

import dev.samkist.renzhe.Manager;
import dev.samkist.renzhe.command.lib.CommandContext;
import dev.samkist.renzhe.command.lib.CommandRole;
import dev.samkist.renzhe.data.DepartmentContext;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.List;
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
		List<Role> memberRoles = member.getRoles();
		List<String> ownedPermissions = loadStaffCommandRoles().stream().filter(commandRole ->
				memberRoles.stream().map(Role::getId)
						.collect(Collectors.toList()).contains(commandRole.getId())).map(CommandRole::getName).collect(Collectors.toList());
		Role highestRole = member.getGuild().getRoleById(ownedPermissions.get(ownedPermissions.size()-1));
		return highestRole;
	}

	public static Boolean isSuperiorRank(Member a, Member b) {
		return userPermissionIndex(a) > userPermissionIndex(b);
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
		List<String> ownedPermissions = loadStaffCommandRoles().stream().filter(commandRole ->
				memberRoles.stream().map(Role::getId)
						.collect(Collectors.toList()).contains(commandRole.getId())).map(CommandRole::getName).collect(Collectors.toList());
		String highestPermission = ownedPermissions.get(ownedPermissions.size()-1);
		List<String> staffRolesByName = loadStaffCommandRoles().stream().map(CommandRole::getName).collect(Collectors.toList());
		return staffRolesByName.indexOf(highestPermission);
	}

	public static List<Role> staffRoles() {
		return loadStaffCommandRoles().stream().map(cr -> getMainGuild().getRoleById(cr.getId())).collect(Collectors.toList());
	}

	public static Integer permissionIndex(String permission) {
		List<String> staffRolesByName = loadStaffCommandRoles().stream().map(CommandRole::getName).collect(Collectors.toList());
		return staffRolesByName.indexOf(permission);
	}

	public static Integer permissionIndex(CommandContext context) {
		return permissionIndex(context.requiredPermission());
	}

	public static boolean hasStaffPermission(Member member, CommandContext context) {
		return hasStaffPermission(member, context.requiredPermission());
	}

	public static Role roleByPermission(String permission) {
		String roleId = loadStaffCommandRoles().stream().filter(r -> {
			System.out.println(r.getName() + " : " + permission);
			return r.getName().equalsIgnoreCase(permission); }).findFirst().orElse(null).getId();
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
		List<DepartmentContext> departmentContexts = loadDepartmentContexts();
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

	public static EmbedBuilder noSuchPlayerEmbed(CommandContext context) {
		EmbedBuilder embed = ConfigManager.defaultEmbed()
				.setColor(Color.red)
				.addField("**Error**", "`Invalid Player`", true)
				.addField("**Usage**", "`" + context.usage() + "`", true);
		return embed;
	}
}
