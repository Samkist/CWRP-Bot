package dev.samkist.renzhe.data;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;

@Entity(value = "mute", useDiscriminator = false)
public class MuteData {
	@Id
	private String discordId;
	private String reason;
	private String staffId;
	private long expires;

	public MuteData(String discordId, String reason, String staffId, long expires) {
		this.discordId = discordId;
		this.reason = reason;
		this.staffId = staffId;
		this.expires = expires;
	}

	public String discordId() {
		return this.discordId;
	}

	public void discordId(String discordId) {
		this.discordId = discordId;
	}

	public String reason() {
		return reason;
	}

	public void reason(String reason) {
		this.reason = reason;
	}

	public String staffId() {
		return this.staffId;
	}

	public void staffId(String staffId) {
		this.staffId = staffId;
	}

	public long expires() {
		return expires;
	}

	public void expires(long expires) {
		this.expires = expires;
	}
}
