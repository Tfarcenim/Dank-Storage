package tfar.dankstorage.utils;

public enum DankStats {
    zero(0, 0),
    one(9, 256),
    two(18, 1024),
    three(27, 4096),
    four(36, 16384),
    five(45, 65536),
    six(54, 262144),
    seven(81, Integer.MAX_VALUE);

    public int slots;
    public int stacklimit;

    DankStats(int slots, int stacklimit) {
        this.slots = slots;
        this.stacklimit = stacklimit;
    }

    public void set(int slots, int stacklimit) {
        this.slots = slots;
        this.stacklimit = stacklimit;
    }
}
