package tfar.dankstorage.inventory;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;
import tfar.dankstorage.world.DankInventoryForge;

public class DankSlot extends SlotItemHandler {

    public DankSlot(DankInventoryForge itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    @Override
    public int getMaxStackSize(@NotNull ItemStack stack) {
        return ((DankInventoryForge)getItemHandler()).getStackLimit(getSlotIndex(),stack);
    }
}