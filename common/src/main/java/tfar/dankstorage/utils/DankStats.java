package tfar.dankstorage.utils;

import tfar.dankstorage.DankStorageConfig;

import java.util.function.Supplier;

public enum DankStats {
    zero(0, () -> 0),
    one(9, DankStorageConfig.SERVER.stacklimit1),
    two(18, DankStorageConfig.SERVER.stacklimit2),
    three(27, DankStorageConfig.SERVER.stacklimit3),
    four(36, DankStorageConfig.SERVER.stacklimit4),
    five(45, DankStorageConfig.SERVER.stacklimit5),
    six(54, DankStorageConfig.SERVER.stacklimit6),
    seven(81, DankStorageConfig.SERVER.stacklimit7);

    private int slots;
    private final Supplier<Integer> stacklimit;

    public int slots(){
        return slots;
    }

    public int stacklimit(){
        return stacklimit.get();
    }

    DankStats(int slots, Supplier<Integer> stacklimit) {
        this.slots = slots;
        this.stacklimit = stacklimit;
    }
}
