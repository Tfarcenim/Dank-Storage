package tfar.dankstorage.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class DankSlot extends SlotItemHandler {
  private final int index;

  public DankSlot(DankHandler itemHandler, int index, int xPosition, int yPosition) {
    super(itemHandler, index, xPosition, yPosition);
    this.index = index;
  }

  @Override
  public boolean canTakeStack(PlayerEntity playerIn) {
    return true;
  }

  @Override
  public int getItemStackLimit(@Nonnull ItemStack stack) {
    return ((DankHandler)this.getItemHandler()).getStackLimit(this.index, stack);
  }

  @Override
  public boolean isSameInventory(Slot other) {
    return other instanceof DankSlot && ((DankSlot) other).getItemHandler() == this.getItemHandler();
  }
}