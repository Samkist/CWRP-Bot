package dev.samkist.renzhe.utils;

public enum Emoji {

	BELL("\uD83D\uDD14"),
	HASH("\u0023\u20E3"),
	WHITE_CHECK_MARK("\u2705"),
	DOWN_ARROW("\u2b07"),
	EXCLAMATION("\u2757"),
	NO_ENTRY("\uD83D\uDEAB"),
	MUSICAL_NOTE("\uD83C\uDFB5"),
	NO_PED("\uD83D\uDEB7"),
	X("\u274C"),
	RADIO_BUTTON("\uD83D\uDD18"),
	NO_GOOD("\uD83D\uDE45"),
	NOTES("\uD83C\uDFB6"),
	ONE_TWO_THREE_FOUR("\uD83D\uDD22"),
	SKIP_FORWARD("\u23E9"),
	SKIP_BACKWARD("\u23EA"),
	THUMBS_UP("\uD83D\uDC4D"),
	SPEAKER("\uD83D\uDD0A"),
	MAIL_BOX("\uD83D\uDCED"),
	REWIND_TO_START("\u23EE"),
	MAG_RIGHT("\uD83D\uDD0E"),
	PENCIL2("\u270F"),
	HEAVY_CHECK_MARK("\u2714"),
	RECYCLE("\u267B"),
	HEADPHONES("\uD83C\uDFA7"),
	HAND_PALM("\u270B"),
	VOLUME_SLIDER("\uD83C\uDF9A"),
	OK_HAND("\uD83D\uDC4C"),
	WARNING("\u26A0"),
	PAUSE_PLAY("\u23EF"),
	PAUSE("\u23F8"),
	MAG_LEFT("\uD83D\uDD0D"),
	LOOP_ONE("\uD83D\uDD02"),
	EXPLOSION("\uD83D\uDCA5"),
	STOP("\u23F9"),
	TIMER("\u23F1"),
	HEARTBEAT("\uD83D\uDC93"),
	ESCAPED_MUSICAL_NOTE("\u266c"),
	NULL("\u200b"),
	BAN_HAMMER("\uD83D\uDD28");

	private final String emoji;

	Emoji(String emoji) {
		this.emoji = emoji;
	}

	@Override
	public String toString() {
		return this.emoji;
	}

}
