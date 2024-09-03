package tfar.dankstorage.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class DankSlot extends Slot {

    private static final Container emptyInventory = new SimpleContainer(0);
    private final DankInventory itemHandler;

    public DankSlot(DankInventory itemHandler, int index, int xPosition, int yPosition) {
        super(emptyInventory, index, xPosition, yPosition);
        this.itemHandler = itemHandler;
    }

    public boolean mayPlace(ItemStack stack) {
        return !stack.isEmpty() && this.itemHandler.canPlaceItem(this.index, stack);
    }

    public ItemStack getItem() {
        return itemHandler.getItemDank(this.index);
    }

    public void set(ItemStack stack) {
        itemHandler.setItemDank(this.index, stack);
        this.setChanged();
    }

    public void initialize(ItemStack stack) {
        itemHandler.setItemDank(this.index, stack);
        this.setChanged();
    }

    public void onQuickCraft(ItemStack oldStackIn, ItemStack newStackIn) {
    }

    public int getMaxStackSize() {
        return this.itemHandler.getMaxStackSizeDank();
    }

    public boolean mayPickup(Player playerIn) {
        return true;
    }

    public ItemStack remove(int amount) {
        return this.itemHandler.extractStack(this.index, amount, false);
    }

    @Override
    public int getMaxStackSize(@NotNull ItemStack stack) {
        return itemHandler.getMaxStackSizeSensitive(stack);
    }
}