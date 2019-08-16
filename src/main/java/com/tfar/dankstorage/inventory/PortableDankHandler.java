package com.tfar.dankstorage.inventory;

import com.tfar.dankstorage.network.NetworkUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import java.lang.reflect.Constructor;

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
      boolean pickup = NetworkUtils.autoPickup(bag);
      boolean Void = NetworkUtils.autoVoid(bag);
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
