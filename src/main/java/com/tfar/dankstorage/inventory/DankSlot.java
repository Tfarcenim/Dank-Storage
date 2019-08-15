package com.tfar.dankstorage.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class DankSlot extends Slot {
  private static IInventory emptyInventory = new Inventory(0);
  private final DankHandler itemHandler;
  private final int index;

  public DankSlot(DankHandler itemHandler, int index, int xPosition, int yPosition) {
    super(emptyInventory, index, xPosition, yPosition);
    this.itemHandler = itemHandler;
    this.index = index;
  }

  @Override
  public boolean isItemValid(@Nonnull ItemStack stack) {
    if (stack.isEmpty()) return false;

    ItemStack currentStack = getItemHandler().getStackInSlot(index);

    getItemHandler().setStackInSlot(index, ItemStack.EMPTY);

    ItemStack remainder = getItemHandler().insertItem(index, stack, true);

    getItemHandler().setStackInSlot(index, currentStack);

    return remainder.isEmpty() || remainder.getCount() < stack.getCount();
  }

  @Override
  @Nonnull
  public ItemStack getStack() {
    return this.getItemHandler().getStackInSlot(index);
  }

  @Override
  public void putStack(@Nonnull ItemStack stack) {
    this.getItemHandler().setStackInSlot(index, stack);
    this.onSlotChanged();
  }

  @Override
  public void onSlotChange(@Nonnull ItemStack p_75220_1_, @Nonnull ItemStack p_75220_2_) {
    getItemHandler().onContentsChanged(index);
  }

  @Override
  public int getSlotStackLimit() {
    return this.itemHandler.getSlotLimit(this.index);
  }

  @Override
  public int getItemStackLimit(@Nonnull ItemStack stack) {
    return this.itemHandler.getStackLimit(this.index, stack);
  }

  @Override
  public boolean canTakeStack(PlayerEntity playerIn) {
    return !this.getItemHandler().extractItem(index, 1, true).isEmpty();
  }

  @Override
  @Nonnull
  public ItemStack decrStackSize(int amount) {
    return this.getItemHandler().extractItem(index, amount, false);
  }

  public DankHandler getItemHandler() {
    return itemHandler;
  }

  @Override
  public boolean isSameInventory(Slot other) {
    return other instanceof DankSlot && ((DankSlot) other).getItemHandler() == this.itemHandler;
  }
}
