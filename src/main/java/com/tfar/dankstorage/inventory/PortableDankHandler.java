package com.tfar.dankstorage.inventory;

import com.tfar.dankstorage.network.CMessageTogglePickup;
import com.tfar.dankstorage.network.Utils;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import java.util.stream.IntStream;

public class PortableDankHandler extends DankHandler {

  public final ItemStack bag;
  public final boolean manual;

  public PortableDankHandler(ItemStack bag, boolean manual) {
    this(Utils.getSlotCount(bag),Utils.getStackLimit(bag),bag,manual);
  }

  protected PortableDankHandler(int size, int stacklimit, ItemStack bag, boolean manual) {
    super(size,stacklimit);
    this.bag = bag;
    this.manual = manual;
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
      int construction = Utils.getUseType(bag).ordinal();
      int selectedSlot = Utils.getSelectedSlot(bag);
      boolean tag = Utils.tag(bag);
      bag.setTag(serializeNBT());
      bag.getOrCreateTag().putInt("mode",mode);
      bag.getOrCreateTag().putInt("construction",construction);
      bag.getOrCreateTag().putInt("selectedSlot",selectedSlot);
      bag.getOrCreateTag().putBoolean("tag",tag);
    }
  }

  @Nonnull
  @Override
  public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
    CMessageTogglePickup.Mode mode = Utils.getMode(bag);
    if (mode == CMessageTogglePickup.Mode.NORMAL || mode == CMessageTogglePickup.Mode.PICKUP_ALL || manual)
    return super.insertItem(slot, stack, simulate);
    ItemStack existing = this.getStackInSlot(slot);
    if (ItemHandlerHelper.canItemStacksStack(stack,existing) || ( Utils.tag(bag) && Utils.doItemStacksShareWhitelistedTags(stack,existing))){
      int stackLimit = this.stacklimit;
      int total = stack.getCount() + existing.getCount();
      int remainder = total - stackLimit;
      if (remainder <= 0) {
        if (!simulate)this.stacks.set(slot, ItemHandlerHelper.copyStackWithSize(existing, total));
        return ItemStack.EMPTY;
      }
      else {
        if (!simulate) this.stacks.set(slot, ItemHandlerHelper.copyStackWithSize(stack, stackLimit));
        if (mode == CMessageTogglePickup.Mode.VOID_PICKUP) return ItemStack.EMPTY;
        return ItemHandlerHelper.copyStackWithSize(stack, remainder);
      }
    } else if (existing.isEmpty() && mode == CMessageTogglePickup.Mode.FILTERED_PICKUP && this.containsItem(stack.getItem())){
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
