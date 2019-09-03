package com.tfar.dankstorage.network;

import com.tfar.dankstorage.block.DankItemBlock;
import com.tfar.dankstorage.inventory.DankHandler;
import com.tfar.dankstorage.inventory.PortableDankHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class Utils {

  public static boolean autoPickup(ItemStack bag) {
    return bag.getItem() instanceof DankItemBlock && bag.hasTag() && bag.getTagCompound().getBoolean("pickup");
  }

  public static boolean construction(ItemStack bag) {
    return bag.getItem() instanceof DankItemBlock && bag.hasTag() && bag.getTagCompound().getBoolean("construction");
  }

  public static boolean autoVoid(ItemStack bag) {
    return bag.getItem() instanceof DankItemBlock && bag.hasTag() && bag.getTagCompound().getBoolean("void");
  }

  public static int getSelectedSlot(ItemStack bag) {
    return bag.getOrCreateTag().getInt("selectedSlot");
  }

  public static void setSelectedSlot(ItemStack bag, int slot) {
    bag.getOrCreateTag().setInteger("selectedSlot", slot);
  }

  public static int getSlotCount(ItemStack bag) {
    switch (getTier(bag)) {
      case 1:
      default:
      case 2:
      case 3:
      case 4:
      case 5:
      case 6:return 9 * getTier(bag);
      case 7:
        return 81;
    }
  }

  public static int getStackLimit(ItemStack bag) {

    switch (getTier(bag)) {
      case 1:
      default:
        return 256;
      case 2:
        return 1024;
      case 3:
        return 4096;
      case 4:
        return 16384;
      case 5:
        return 65536;
      case 6:
        return 262144;
      case 7:
        return Integer.MAX_VALUE;
    }
  }

  public static int getTier(ItemStack bag) {
    return getTier(bag.getItem().getRegistryName());
  }

  public static int getTier(ResourceLocation registryname) {
    return Integer.parseInt(registryname.getPath().substring(5));
  }

  public static void changeSlot(ItemStack bag, boolean right) {
    int selectedSlot = getSelectedSlot(bag);
    DankHandler handler = getHandler(bag);
    if (right) {
      selectedSlot++;
      if (selectedSlot >= handler.getSlots()) selectedSlot = 0;
    } else {
      selectedSlot--;
      if (selectedSlot < 0) selectedSlot = handler.getSlots() - 1;
    }
    setSelectedSlot(bag, selectedSlot);
  }

  public static PortableDankHandler getHandler(ItemStack bag) {
    return new PortableDankHandler(bag);
  }
}
