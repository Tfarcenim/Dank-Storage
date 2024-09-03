package tfar.dankstorage.world;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;
import tfar.dankstorage.inventory.DankInventory;
import tfar.dankstorage.utils.DankStats;

public class DankInventoryForge extends DankInventory implements IItemHandlerModifiable {


    public DankInventoryForge(DankStats stats, DankSavedData data) {
        super(stats,data);
    }

    @Override
    public int getSlots() {
        return items.size();
    }

    @Override
    public ItemStack getStackInSlot(int i) {
        return items.get(i);
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        return insertStack(slot,stack,simulate);
    }

    @Override
    public ItemStack extractItem(int i, int i1, boolean b) {
        return extractStack(i,i1,b);
    }

    @Override
    public int getSlotLimit(int slot) {
        return capacity;
    }

    @Override
    public boolean isItemValid(int i, ItemStack itemStack) {
        return canPlaceItem(i,itemStack);
    }

    @Override
    public void setStackInSlot(int i, ItemStack itemStack) {
        setItemDank(i,itemStack);
    }
}
