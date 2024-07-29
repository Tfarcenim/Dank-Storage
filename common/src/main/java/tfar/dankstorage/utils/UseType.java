package tfar.dankstorage.utils;

import net.minecraft.util.StringRepresentable;

public enum UseType implements StringRepresentable {
    bag, construction;
    public static final UseType[] VALUES = UseType.values();

    @Override
    public String getSerializedName() {
        return name();
    }
}
