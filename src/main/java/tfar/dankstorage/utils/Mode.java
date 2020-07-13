package tfar.dankstorage.utils;

public enum Mode {
	NORMAL (0xffffff), PICKUP_ALL (0x00ff00), FILTERED_PICKUP (0xffff00), VOID_PICKUP (0xff0000);

	private final int color;

	Mode(int color) {
		this.color = color;
	}

	public float r() {
		return (color >> 16 & 0xff)/255f;
	}

	public float g() {
		return (color >> 8 & 0xff)/255f;
	}

	public float b() {
		return (color & 0xff)/255f;
	}

	public Mode cycle() {
		if (ordinal() < values().length -1)
			return values()[ordinal()+1];
		return NORMAL;
	}

}