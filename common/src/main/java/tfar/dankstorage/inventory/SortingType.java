package tfar.dankstorage.inventory;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;

import java.util.Comparator;

public enum SortingType {

    descending((stack1, stack2) -> stack2.getCount() - stack1.getCount()),
    ascending(Comparator.comparingInt(ItemStack::getCount)),
    registry_name((stack1,stack2) -> {
        String path1 = BuiltInRegistries.ITEM.getKey(stack1.getItem()).getPath();
        String path2 = BuiltInRegistries.ITEM.getKey(stack2.getItem()).getPath();
        return path1.toString().compareTo(path2.toString());
    });

    public final Comparator<ItemStack> comparator;

    SortingType(Comparator<ItemStack> comparator) {
        this.comparator = comparator;
    }

}
