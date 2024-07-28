package tfar.dankstorage.utils;

import net.minecraft.ChatFormatting;

public enum TxtColor {
    INVALID(ChatFormatting.RED), TOO_HIGH(ChatFormatting.GOLD),
    DIFFERENT_TIER(ChatFormatting.YELLOW), GOOD(ChatFormatting.GREEN),
    LOCKED(ChatFormatting.BLUE);
    public final int color;

    TxtColor(int color) {
        this.color = color;
    }

    TxtColor(ChatFormatting color) {
        this(color.getColor());
    }
}
