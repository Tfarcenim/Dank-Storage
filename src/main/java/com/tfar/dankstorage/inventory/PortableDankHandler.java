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

      int mode = Utils.getMode(bag).ordinal();
      int construction = Utils.getUseType(bag).ordinal();
      int selectedSlot = Utils.getSelectedSlot(bag);
      boolean tag = Utils.tag(bag);
      CompoundNBT nbt = null;
      if (bag.hasDisplayName()) nbt = bag.getChildTag("display");

      bag.setTag(serializeNBT());
      if (nbt != null)bag.getOrCreateTag().put("display",nbt);
      bag.getOrCreateTag().putInt("mode",mode);
      bag.getOrCreateTag().putInt("construction",construction);
      bag.getOrCreateTag().putInt("selectedSlot",selectedSlot);
      bag.getOrCreateTag().putBoolean("tag",tag);

  }

  public void readItemStack() {
    if (bag.hasTag()) {
      deserializeNBT(bag.getTag());
    }
  }

  @Override
  public void onContentsChanged(int slot) {
    this.writeItemStack();
  }
}
