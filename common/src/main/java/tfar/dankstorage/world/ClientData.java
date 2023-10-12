package tfar.dankstorage.world;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;

public class ClientData {

    public static ItemStack selectedItem = ItemStack.EMPTY;

    public static NonNullList<ItemStack> cachedItems;

    public static void setData(ItemStack selected) {
        selectedItem = selected;
    }

    public static void setList(NonNullList<ItemStack> stacks) {
        cachedItems = stacks;
    }
}
