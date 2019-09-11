package com.tfar.dankstorage.network;

import com.tfar.dankstorage.block.DankItemBlock;
import com.tfar.dankstorage.inventory.DankHandler;
import com.tfar.dankstorage.inventory.PortableDankHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Locale;

import static com.tfar.dankstorage.network.CMessageToggle.*;

public class Utils {

  public static Mode getMode(ItemStack bag) {
    return modes[bag.getOrCreateTag().getInt("mode")];
  }

  //0,1,2,3
  public static void cycleMode(ItemStack bag, PlayerEntity player) {
    int ordinal = bag.getOrCreateTag().getInt("mode");
    ordinal++;
    if (ordinal > 3) ordinal = 0;
    bag.getOrCreateTag().putInt("mode", ordinal);
    player.sendStatusMessage(
            new TranslationTextComponent("dankstorage.mode." + modes[ordinal].name().toLowerCase(Locale.ROOT)), true);
  }

  public static boolean construction(ItemStack bag) {
    return bag.getItem() instanceof DankItemBlock && bag.hasTag() && bag.getTag().getBoolean("construction");
  }

  public static int getSelectedSlot(ItemStack bag) {
    return bag.getOrCreateTag().getInt("selectedSlot");
  }

  public static void setSelectedSlot(ItemStack bag, int slot) {
    bag.getOrCreateTag().putInt("selectedSlot", slot);
  }

  public static int getSlotCount(ItemStack bag) {
    int tier = getTier(bag);
      if (tier > 0 && tier < 7)
        return 9 * tier;
      if (tier == 7)
        return 81;
      throw new IndexOutOfBoundsException("tier "+ tier +" is out of bounds!");
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
    DankHandler handler = getHandler(bag,false);
    if (right) {
      selectedSlot++;
      if (selectedSlot >= handler.getSlots()) selectedSlot = 0;
    } else {
      selectedSlot--;
      if (selectedSlot < 0) selectedSlot = handler.getSlots() - 1;
    }
    setSelectedSlot(bag, selectedSlot);
  }

  public static PortableDankHandler getHandler(ItemStack bag,boolean manual) {
    return new PortableDankHandler(bag,manual);
  }
}
