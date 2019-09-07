package com.tfar.dankstorage.inventory;

import com.tfar.dankstorage.network.Utils;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

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
      boolean pickup = Utils.canPickup(bag);
      boolean isVoid = Utils.autoVoid(bag);
      boolean construction = Utils.construction(bag);
      int selectedSlot = Utils.getSelectedSlot(bag);
      bag.setTag(serializeNBT());
      bag.getOrCreateTag().putBoolean("pickup",pickup);
      bag.getOrCreateTag().putBoolean("void",isVoid);
      bag.getOrCreateTag().putBoolean("construction",construction);
      bag.getOrCreateTag().putInt("selectedSlot",selectedSlot);
    }
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
