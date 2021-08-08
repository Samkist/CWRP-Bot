package dev.samkist.renzhe.data;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;

@Entity(value = "lockdown", useDiscriminator = false)
public class LockdownData {

	@Id
	private String channelId;
	private long expires;

	public LockdownData(String channelId, long expires) {
		this.channelId = channelId;
		this.expires = expires;
	}

	public String channelId() {
		return this.channelId;
	}

	public void channelId(String channelId) {
		this.channelId = channelId;
	}

	public long expires() {
		return this.expires;
	}

	public void expires(long expires) {
		this.expires = expires;
	}
}
