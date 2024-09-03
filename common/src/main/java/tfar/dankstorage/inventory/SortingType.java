package tfar.dankstorage.inventory;

import net.minecraft.world.item.ItemStack;

import java.util.Comparator;

public enum SortingType {

    descending((stack1, stack2) -> stack2.getCount() - stack1.getCount()),
    ascending(Comparator.comparingInt(ItemStack::getCount));

    public final Comparator<ItemStack> comparator;

    SortingType(Comparator<ItemStack> comparator) {
        this.comparator = comparator;
    }

}
