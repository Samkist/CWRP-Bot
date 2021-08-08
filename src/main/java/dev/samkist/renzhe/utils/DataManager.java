package dev.samkist.renzhe.utils;

import dev.samkist.renzhe.data.LockdownData;
import dev.samkist.renzhe.data.MuteData;

import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

public class DataManager {
	private static HashMap<String, MuteData> mutes;
	private static HashMap<String, LockdownData> lockdowns;

	public static HashMap<String, MuteData> getMutes() {
		if(Objects.isNull(mutes)) {
			return loadMutes();
		}

		return mutes;
	}

	public static void saveMute(MuteData mute) {
		mutes.put(mute.discordId(), mute);
		DBManager.saveMute(mute);
	}

	public static void revokeMute(String discordId) {
		mutes.remove(discordId);
		DBManager.revokeMute(discordId);
	}

	public static Optional<MuteData> getMute(String discordId) {
		return Optional.ofNullable(mutes.computeIfAbsent(discordId, (id) -> DBManager.getMute(id).orElse(null)));
	}

	private static HashMap<String, MuteData> loadMutes() {
		mutes = DBManager.loadMutes();
		return mutes;
	}

	public static HashMap<String, LockdownData> getLockdowns() {
		if(Objects.isNull(mutes)) {
			return loadLockdowns();
		}

		return lockdowns;
	}

	public static void saveLockdown(LockdownData lockdown) {
		lockdowns.put(lockdown.channelId(), lockdown);
		DBManager.saveLockdown(lockdown);
	}

	public static void revokeLockdown(String channelId) {
		lockdowns.remove(channelId);
		DBManager.revokeLockdown(channelId);
	}

	public static Optional<LockdownData> getLockdown(String channelId) {
		return Optional.ofNullable(lockdowns.computeIfAbsent(channelId, (id) -> DBManager.getLockdown(id).orElse(null)));
	}

	private static HashMap<String, LockdownData> loadLockdowns() {
		lockdowns = DBManager.loadLockdowns();
		return lockdowns;
	}
}
