package tfar.dankstorage.utils;

import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public record ItemStackWrapper(ItemStack stack) implements Comparable<ItemStackWrapper> {

    @Override
    public int compareTo(@Nonnull ItemStackWrapper wrapper) {
        return wrapper.stack.getCount() - this.stack.getCount();
    }
}
