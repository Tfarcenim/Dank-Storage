package com.tfar.dankstorage.inventory;

import com.tfar.dankstorage.network.CMessageToggle;
import com.tfar.dankstorage.network.Utils;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import java.util.stream.IntStream;

public class PortableDankHandler extends DankHandler {

  public final ItemStack bag;

  public PortableDankHandler(ItemStack bag) {
    this(Utils.getSlotCount(bag),Utils.getStackLimit(bag),bag);
  }

  protected PortableDankHandler(int size, int stacklimit, ItemStack bag) {
    super(size,stacklimit);
    this.bag = bag;
    readItemStack();
  }

  public void writeItemStack() {
    if (false) {
      return;
    }
    if (bag.isEmpty()) {
      if (bag.hasTag()) {
        bag.getTag().remove("Items");
        if (bag.getTag().isEmpty()) {
          bag.setTag(null);
        }
      }
    } else {
      int mode = Utils.getMode(bag).ordinal();
      boolean construction = Utils.construction(bag);
      int selectedSlot = Utils.getSelectedSlot(bag);
      bag.setTag(serializeNBT());
      bag.getOrCreateTag().putInt("mode",mode);
      bag.getOrCreateTag().putBoolean("construction",construction);
      bag.getOrCreateTag().putInt("selectedSlot",selectedSlot);
    }
  }

  @Nonnull
  @Override
  public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
    CMessageToggle.Mode mode = Utils.getMode(bag);
    if (mode == CMessageToggle.Mode.NORMAL || mode == CMessageToggle.Mode.PICKUP_ALL)
    return super.insertItem(slot, stack, simulate);
    ItemStack existing = this.getStackInSlot(slot);
    if (ItemHandlerHelper.canItemStacksStack(stack,existing)){
      int stackLimit = this.stacklimit;
      int total = stack.getCount() + existing.getCount();
      int remainder = total - stackLimit;
      if (remainder <= 0) {
        if (!simulate)this.stacks.set(slot, ItemHandlerHelper.copyStackWithSize(stack, total));
        return ItemStack.EMPTY;
      }
      else {
        if (!simulate) this.stacks.set(slot, ItemHandlerHelper.copyStackWithSize(stack, stackLimit));
        if (mode == CMessageToggle.Mode.VOID_PICKUP) return ItemStack.EMPTY;
        return ItemHandlerHelper.copyStackWithSize(stack, remainder);
      }
    } else if (existing.isEmpty() && mode == CMessageToggle.Mode.FILTERED_PICKUP && this.containsItem(stack.getItem())){
      if (!simulate)this.stacks.set(slot, stack);
      return ItemHandlerHelper.copyStackWithSize(stack,stack.getCount() - this.getStackLimit(slot,stack));
    } else return stack;
  }

  public boolean containsItem(Item item){
    return IntStream.range(0, this.getSlots()).anyMatch(i -> this.getStackInSlot(i).getItem() == item);
  }

  @Override
  public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
    super.setStackInSlot(slot, stack);
    this.writeItemStack();
  }

  public void readItemStack() {
    if (bag.hasTag()) {
      deserializeNBT(bag.getTag());
    }
  }
}
