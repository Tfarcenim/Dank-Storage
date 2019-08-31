package com.tfar.dankstorage.inventory;

import com.tfar.dankstorage.network.Utils;
import net.minecraft.item.ItemStack;

public class PortableDankHandler extends DankHandler {

  public final ItemStack bag;

  public PortableDankHandler(int size, int stacklimit, ItemStack bag) {
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
      boolean pickup = Utils.autoPickup(bag);
      boolean Void = Utils.autoVoid(bag);
      bag.setTag(serializeNBT());
      bag.getOrCreateTag().putBoolean("pickup",pickup);
      bag.getOrCreateTag().putBoolean("void",Void);
    }
  }
  public void readItemStack() {
    if (bag.hasTag()) {
      deserializeNBT(bag.getTag());
    }
  }
}
