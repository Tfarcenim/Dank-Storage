package tfar.dankstorage.utils;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.StringRepresentable;

public enum PickupMode implements StringRepresentable {
    none(0xffffff), pickup_all(0x00ff00), filtered_pickup(0xffff00), void_pickup(0xff0000);

    public static final PickupMode[] VALUES = values();
    private final int color;

    PickupMode(int color) {
        this.color = color;
    }

    public float r() {
        return (color >> 16 & 0xff) / 255f;
    }

    public float g() {
        return (color >> 8 & 0xff) / 255f;
    }

    public float b() {
        return (color & 0xff) / 255f;
    }

    public int getColor() {
        return color;
    }

    public MutableComponent translate() {
        return Component.translatable("dankstorage.mode."+ this);
    }

    @Override
    public String getSerializedName() {
        return name();
    }
}