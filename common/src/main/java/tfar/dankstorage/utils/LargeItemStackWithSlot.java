package tfar.dankstorage.utils;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;

public record LargeItemStackWithSlot(int slot, ItemStack stack) {
public static final Codec<LargeItemStackWithSlot> CODEC = RecordCodecBuilder.create(
        i -> i.group(
                        ExtraCodecs.UNSIGNED_BYTE.fieldOf("Slot").orElse(0).forGetter(LargeItemStackWithSlot::slot),
                        SerializationHelper.LARGE_MAP_CODEC.forGetter(LargeItemStackWithSlot::stack)
                )
                .apply(i, LargeItemStackWithSlot::new)
);

public boolean isValidInContainer(int containerSize) {
    return this.slot >= 0 && this.slot < containerSize;
}
}
