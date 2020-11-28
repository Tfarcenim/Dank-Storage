package tfar.dankstorage.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

/**
 * vanilla doesn't check the itemstack's max size when shiftclicking items to a slot, this patch enforces that
 */
public class CappedSlot extends Slot {
    public CappedSlot(IInventory inventoryIn, int index, int xPosition, int yPosition) {
        super(inventoryIn, index, xPosition, yPosition);
    }

    @Override
    public int getItemStackLimit(ItemStack stack) {
        return Math.min(getSlotStackLimit(),stack.getMaxStackSize());
    }
}
