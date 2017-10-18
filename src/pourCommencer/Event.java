package pourCommencer;

public enum Event {
	DUST_GENERATED(EventType.SIMULATOR),
	DUST_VACCUMED(EventType.AGENT),
	JEWELRY_GENERATED(EventType.SIMULATOR),
	JEWELRY_GATHERED(EventType.AGENT),
	JEWELRY_VACCUMED(EventType.AGENT),
	AGENT_MOVED(EventType.AGENT),
	AGENT_HIT_NORTH(EventType.AGENT),
	AGENT_HIT_SOUTH(EventType.AGENT),
	AGENT_HIT_WEST(EventType.AGENT),
	AGENT_HIT_EST(EventType.AGENT),
	VOID_VACCUMED(EventType.AGENT),
	VOID_GATHERED(EventType.AGENT);

	EventType type;

	Event(EventType type) {
		this.type = type;
	}

	public EventType getType() {
		return type;
	}
}
