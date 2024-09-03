package tfar.dankstorage.world;

import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ClientData {

    public static List<ItemStack> cachedItems;

    public static void setList(List<ItemStack> stacks) {
        cachedItems = stacks;
    }
}
