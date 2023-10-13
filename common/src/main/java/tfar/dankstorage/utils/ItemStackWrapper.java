package tfar.dankstorage.utils;

import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public class ItemStackWrapper implements Comparable<ItemStackWrapper> {
    public final ItemStack stack;

    public ItemStackWrapper(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public int compareTo(@Nonnull ItemStackWrapper wrapper) {
        return wrapper.stack.getCount() - this.stack.getCount();
    }
}
