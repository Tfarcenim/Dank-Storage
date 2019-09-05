package com.tfar.dankstorage.util;

import com.tfar.dankstorage.DankStorage;
import com.tfar.dankstorage.block.DankItemBlock;
import com.tfar.dankstorage.inventory.DankHandler;
import com.tfar.dankstorage.inventory.PortableDankHandler;
import com.tfar.dankstorage.network.CMessageToggle;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;

public class Utils {

  public static boolean autoPickup(ItemStack bag) {
    return bag.getItem() instanceof DankItemBlock && bag.hasTagCompound() && bag.getTagCompound().getBoolean("pickup");
  }

  public static boolean construction(ItemStack bag) {
    return bag.getItem() instanceof DankItemBlock && bag.hasTagCompound() && bag.getTagCompound().getBoolean("construction");
  }

  public static boolean autoVoid(ItemStack bag) {
    return bag.getItem() instanceof DankItemBlock && bag.hasTagCompound() && bag.getTagCompound().getBoolean("void");
  }

  public static void toggle(ItemStack bag, CMessageToggle.KeybindToggleType t){
    String s;
    switch (t){case VOID:s = "void";break;
      case PICKUP:s = "pickup"; break;
      case CONSTRUCTION:s = "construction"; break;
      default:{
        DankStorage.LOGGER.error("invalid keybind press");
        return;
      }
    }
    boolean toggle = getTagSafely(bag).getBoolean(s);
    Minecraft.getMinecraft().player.sendStatusMessage(new TextComponentTranslation("dankstorage."+s+"." + (toggle ? "disabled" : "enabled")), true);
    bag.getTagCompound().setBoolean(s, !toggle);
  }

  public static NBTTagCompound getTagSafely(ItemStack stack){
    return stack.getTagCompound() == null ? new NBTTagCompound() : stack.getTagCompound();
  }

  public static int getSelectedSlot(ItemStack bag) {
    return getTagSafely(bag).getInteger("selectedSlot");
  }

  public static void setSelectedSlot(ItemStack bag, int slot) {
    getTagSafely(bag).setInteger("selectedSlot", slot);
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
