package tfar.dankstorage.transferapi;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
//shameless clone of ItemResource from neoforge
public record IItemResource(ItemStack inner) {

    public static final IItemResource EMPTY = new IItemResource(ItemStack.EMPTY);

    public IItemResource {

    }


    public static IItemResource of(ItemLike item) {
        Item value = item.asItem();
        if (value == Items.AIR) return EMPTY;
        return new IItemResource(new ItemStack(item));
    }

    public boolean isEmpty() {
        return inner.isEmpty();
    }

    public Item getItem() {
        return inner.getItem();
    }

    public boolean matches(ItemStack stack) {
        return ItemStack.isSameItemSameComponents(stack, inner);
    }
    public ItemStack toStack() {
        return this.inner.copyWithCount(1);
    }

    public ItemStack toStack(int count) {
        //TransferPreconditions.checkNonNegative(count);
        if (count == 0) return ItemStack.EMPTY;
        return this.inner.copyWithCount(count);
    }

}
