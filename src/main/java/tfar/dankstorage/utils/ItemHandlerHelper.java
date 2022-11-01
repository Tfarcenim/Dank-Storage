package tfar.dankstorage.utils;

import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public class ItemHandlerHelper {

    @Nonnull
    public static ItemStack copyStackWithSize(@Nonnull ItemStack itemStack, int size) {
        if (size == 0)
            return ItemStack.EMPTY;
        ItemStack copy = itemStack.copy();
        copy.setCount(size);
        return copy;
    }

    public static boolean canItemStacksStack(@Nonnull ItemStack a, @Nonnull ItemStack b) {
        if (a.isEmpty() || !a.sameItemStackIgnoreDurability(b) || a.hasTag() != b.hasTag())
            return false;
        return (!a.hasTag() || a.getTag().equals(b.getTag()));
    }
}
