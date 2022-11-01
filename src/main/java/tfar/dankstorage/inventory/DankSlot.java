package tfar.dankstorage.inventory;

import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import tfar.dankstorage.world.DankInventory;

public class DankSlot extends Slot {

    private final DankInventory itemHandler;

    public DankSlot(DankInventory itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
        this.itemHandler = itemHandler;
    }

    //need to make the slot respect the inventory
    @Override
    public boolean mayPlace(ItemStack itemStack) {
        return itemHandler.canPlaceItem(index,itemStack) && super.mayPlace(itemStack);
    }

    //make sure items stack as intended
    @Override
    public int getMaxStackSize(ItemStack stack) {
        return super.getMaxStackSize();
    }
}