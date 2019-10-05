package com.tfar.dankstorage.network;

import com.tfar.dankstorage.DankStorage;
import com.tfar.dankstorage.block.DankItemBlock;
import com.tfar.dankstorage.inventory.DankHandler;
import com.tfar.dankstorage.inventory.PortableDankHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Collections;
import java.util.Set;

import static com.tfar.dankstorage.network.CMessageTogglePickup.*;
import static com.tfar.dankstorage.network.CMessageTogglePlacement.useTypes;

public class Utils {

  public static final Tag<Item> WHITELISTED_TAGS = new ItemTags.Wrapper(new ResourceLocation(DankStorage.MODID,"whitelisted_tags"));

  public static Mode getMode(ItemStack bag) {
    return modes[bag.getOrCreateTag().getInt("mode")];
  }

  public static boolean isConstruction(ItemStack bag){
    return bag.getOrCreateTag().getInt("construction") == 0;
  }

  //0,1,2,3
  public static void cycleMode(ItemStack bag, PlayerEntity player) {
    int ordinal = bag.getOrCreateTag().getInt("mode");
    ordinal++;
    if (ordinal > 3) ordinal = 0;
    bag.getOrCreateTag().putInt("mode", ordinal);
    player.sendStatusMessage(
            new TranslationTextComponent("dankstorage.mode." + modes[ordinal].name()), true);
  }

  public static CMessageTogglePlacement.UseType getUseType(ItemStack bag) {
    return useTypes[bag.getOrCreateTag().getInt("construction")];
  }

  //0,1,2
  public static void cyclePlacement(ItemStack bag, PlayerEntity player) {
    int ordinal = bag.getOrCreateTag().getInt("construction");
    ordinal++;
    if (ordinal > 2) ordinal = 0;
    bag.getOrCreateTag().putInt("construction", ordinal);
    player.sendStatusMessage(
            new TranslationTextComponent("dankstorage.construction." + useTypes[ordinal].name()), true);
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
    throw new IndexOutOfBoundsException("tier " + tier + " is out of bounds!");
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
    DankHandler handler = getHandler(bag, true);
    if (right) {
      selectedSlot++;
      if (selectedSlot >= handler.getSlots()) selectedSlot = 0;
    } else {
      selectedSlot--;
      if (selectedSlot < 0) selectedSlot = handler.getSlots() - 1;
    }
    setSelectedSlot(bag, selectedSlot);
  }

  public static boolean tag(ItemStack bag) {
    return bag.getItem() instanceof DankItemBlock && bag.hasTag() && bag.getTag().getBoolean("tag");
  }

  public static PortableDankHandler getHandler(ItemStack bag, boolean manual) {
    return new PortableDankHandler(bag, manual);
  }

  public static ItemStack getItemStackInSelectedSlot(ItemStack bag){
    return getHandler(bag,false).getStackInSlot(Utils.getSelectedSlot(bag));
  }

  public static boolean doItemStacksShareWhitelistedTags(final ItemStack stack1,final ItemStack stack2) {
    if (stack1.hasTag() || stack2.hasTag()) return false;

    if (!stack1.getItem().isIn(WHITELISTED_TAGS) || !stack2.getItem().isIn(WHITELISTED_TAGS))return false;

    Set<ResourceLocation> taglist1 = stack1.getItem().getTags();
    Set<ResourceLocation> taglist2 = stack2.getItem().getTags();
    return !Collections.disjoint(taglist1,taglist2);
  }
}
