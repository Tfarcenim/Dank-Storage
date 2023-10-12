package tfar.dankstorage.utils;

public enum TxtColor {
    INVALID(0xffff0000), TOO_HIGH(0xffff8000), DIFFERENT_TIER(0xffffff00), GOOD(0xff00ff00), LOCKED(0xff0000ff);
    public final int color;

    TxtColor(int color) {
        this.color = color;
    }
}
