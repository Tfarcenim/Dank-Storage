package com.tfar.dankstorage.network;

import com.tfar.dankstorage.block.DankBlock;
import com.tfar.dankstorage.block.DankItemBlock;
import com.tfar.dankstorage.inventory.DankHandler;
import net.minecraft.item.ItemStack;

public class Utils {

  public static boolean autoPickup(ItemStack bag){
   return bag.getItem() instanceof DankItemBlock && bag.hasTag() && bag.getTag().getBoolean("pickup");
  }

  public static boolean construction(ItemStack bag){
    return bag.getItem() instanceof DankItemBlock && bag.hasTag() && bag.getTag().getBoolean("construction");
  }

  public static boolean autoVoid(ItemStack bag) {
    return bag.getItem() instanceof DankItemBlock && bag.hasTag() && bag.getTag().getBoolean("void");
  }

  public static int getSelectedSlot(ItemStack bag){
    return bag.getOrCreateTag().getInt("selectedSlot");
  }

  public static void setSelectedSlot(ItemStack bag, int slot){
    bag.getOrCreateTag().putInt("selectedSlot",slot);
  }

  public static void changeSlot(ItemStack bag, boolean right){
    int selectedSlot = getSelectedSlot(bag);
    DankHandler handler = DankBlock.getHandler(bag);
    if (right){
      selectedSlot++;
      if (selectedSlot >= handler.getSlots()) selectedSlot = 0;
    } else {
      selectedSlot--;
      if (selectedSlot < 0)selectedSlot = handler.getSlots() - 1;
    }
    setSelectedSlot(bag,selectedSlot);
  }
}
