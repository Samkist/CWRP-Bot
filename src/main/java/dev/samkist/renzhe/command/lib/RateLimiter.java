package dev.samkist.renzhe.command.lib;

import dev.samkist.renzhe.Manager;
import net.dv8tion.jda.api.entities.User;

import java.util.HashMap;
import java.util.Map;

public class RateLimiter {
	private static final long LIMIT_PERIOD = 5000;
	private static final int LIMIT_COUNT = 3;

	private static final Object limitMap$lock = new Object[0];
	private static final Map<User, LimitBuffer> limitMap = new HashMap<>();

	static boolean checkIfRateLimited(User user) {
		if (user.getId().equals(Manager.SAMKIST)) return false;

		LimitBuffer userLimit = getLimitBuffer(user);
		long oldestTime = userLimit.getOldestTime();
		long currentTime = System.currentTimeMillis();

		return currentTime - oldestTime < LIMIT_PERIOD;
	}

	private static LimitBuffer getLimitBuffer(User user) {
		synchronized (limitMap$lock) {
			LimitBuffer buffer = limitMap.get(user);
			if (buffer != null) {
				return buffer;
			}
			buffer = new LimitBuffer();
			limitMap.put(user, buffer);
			return buffer;
		}
	}

	private static class LimitBuffer {
		private final long[] timeStamps = new long[LIMIT_COUNT];
		private int lastStamp = 0;

		long getOldestTime() {
			synchronized (this) {
				long time = timeStamps[lastStamp];
				timeStamps[lastStamp] = System.currentTimeMillis();
				lastStamp++;
				if (lastStamp >= LIMIT_COUNT) {
					lastStamp = 0;
				}
				return time;
			}
		}
	}
}
