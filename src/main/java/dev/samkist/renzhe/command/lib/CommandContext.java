package dev.samkist.renzhe.command.lib;

import java.awt.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class CommandContext {
	private final String name;
	private final String description;
	private final String requiredPermission;
	private final String usage;
	private final List<String> triggers;
	private final List<Attribute> attributes;
	private final Color embedColor;
	private final AtomicBoolean visible = new AtomicBoolean(true);

	public CommandContext(String name, String description, String requiredPermission, String usage, List<String> triggers, List<Attribute> attributes, Color embedColor) {
		this.name = name;
		this.description = description;
		this.requiredPermission = requiredPermission;
		this.usage = usage;
		this.triggers = triggers;
		this.attributes = attributes;
		this.embedColor = embedColor;
	}

	public String name() {
		return name;
	}

	public String description() {
		return description;
	}

	public String requiredPermission() {
		return requiredPermission;
	}

	public String usage() {
		return usage;
	}

	public List<String> triggers() {
		return triggers;
	}

	public List<Attribute> attributes() {
		return attributes;
	}

	public Color embedColor() {
		return embedColor;
	}

	public Boolean visible() {
		return visible.get();
	}

	public CommandContext visible(Boolean visible) {
		this.visible.set(visible);
		return this;
	}

	public class Attribute {
		private final String key;
		private final String value;

		public Attribute(String key, String value) {
			this.key = key;
			this.value = value;
		}

		public String key() {
			return key;
		}

		public String value() {
			return value;
		}
	}
}
