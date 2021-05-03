package dev.samkist.renzhe.data;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class Infraction {
	private final int id;
	private final Action action;
	private final String description;
	private final String staffMember;
	private final String staffId;
	private final String targetId;
	private final AtomicReference<String> reason = new AtomicReference<>();
	private final AtomicReference<String> messageId = new AtomicReference<>();
	private final AtomicReference<ArrayList<String>> evidences = new AtomicReference<>(new ArrayList<>());

	public Infraction(int id, Action action, String description, String staffMember, String staffId, String targetId, String reason, String messageId, List<String> evidences) {
		this.id = id;
		this.action = action;
		this.description = description;
		this.staffMember = staffMember;
		this.staffId = staffId;
		this.targetId = targetId;
		this.reason.set(reason);
		this.messageId.set(messageId);
		this.evidences.set((ArrayList<String>) evidences);
	}

	public int id() {
		return id;
	}

	public Action action() {
		return action;
	}

	public String description() {
		return description;
	}

	public String staffMember() {
		return staffMember;
	}

	public String staffId() {
		return staffId;
	}

	public String targetId() {
		return targetId;
	}

	public String reason() {
		return reason.get();
	}

	public void reason(String reason) {
		this.reason.set(reason);
	}

	public String messageId() {
		return this.messageId.get();
	}

	public void messageId(String messageId) {
		this.messageId.set(messageId);
	}
}
