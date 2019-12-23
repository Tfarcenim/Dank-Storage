package com.tfar.dankstorage.inventory;

import com.tfar.dankstorage.utils.Utils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

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
      bag.getOrCreateTag().put(Utils.INV,serializeNBT());
  }

  public void readItemStack() {
      deserializeNBT(bag.getOrCreateChildTag(Utils.INV));
  }

  @Override
  public void onContentsChanged(int slot) {
    this.writeItemStack();
  }
}
