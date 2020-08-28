package tfar.dankstorage.utils;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum DankStats {
	zero(0,0),
	one(9, 256),
	two(18, 1024),
	three(27,4096),
	four(36,16384),
	five(45,65536),
	six(54,262144),
	seven(81,Integer.MAX_VALUE);

	public static final Map<Integer,DankStats> intToStat = Arrays.stream(values()).collect(Collectors.toMap(Enum::ordinal, Function.identity()));

	public int slots;
	public int stacklimit;

	DankStats(int slots, int stacklimit) {
		this.slots = slots;
		this.stacklimit = stacklimit;
	}

	public static DankStats fromInt(int tier) {
		return intToStat.get(tier);
	}

	public void set(int slots, int stacklimit) {
		this.slots = slots;
		this.stacklimit = stacklimit;
	}
}
