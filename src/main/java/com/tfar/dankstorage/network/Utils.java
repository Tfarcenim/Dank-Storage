package com.tfar.dankstorage.network;

import com.tfar.dankstorage.block.DankItemBlock;
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
}
