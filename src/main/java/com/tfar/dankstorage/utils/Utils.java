package com.tfar.dankstorage.utils;

import com.tfar.dankstorage.DankStorage;
import com.tfar.dankstorage.block.DankItemBlock;
import com.tfar.dankstorage.container.AbstractAbstractDankContainer;
import com.tfar.dankstorage.inventory.DankHandler;
import com.tfar.dankstorage.inventory.PortableDankHandler;
import com.tfar.dankstorage.network.CMessageToggleUseType;
import com.tfar.dankstorage.tile.AbstractDankStorageTile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.items.ItemStackHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.tfar.dankstorage.network.CMessageTogglePickup.*;
import static com.tfar.dankstorage.network.CMessageToggleUseType.useTypes;

public class Utils {

  public static final Tag<Item> WHITELISTED_TAGS = new ItemTags.Wrapper(new ResourceLocation(DankStorage.MODID,"whitelisted_tags"));

  public static Mode getMode(ItemStack bag) {
    return modes[bag.getOrCreateTag().getInt("mode")];
  }

  public static boolean isConstruction(ItemStack bag){
    return bag.hasTag() && bag.getTag().contains("construction") && bag.getTag().getInt("construction") == 2;
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

  public static CMessageToggleUseType.UseType getUseType(ItemStack bag) {
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
    return getSlotCount(getTier(bag));
  }

  public static int getSlotCount(int tier){
    if (tier > 0 && tier < 7)
      return 9 * tier;
    if (tier == 7)
      return 81;
    throw new IndexOutOfBoundsException("tier " + tier + " is out of bounds!");
  }

  public static void lockSlot(DankHandler handler, int slot){
    handler.lockedSlots[slot] = 1 - handler.lockedSlots[slot];
  }

  public static void sort( PlayerEntity player){
    if (player == null) return;
      Container openContainer = player.openContainer;
      if (openContainer instanceof AbstractAbstractDankContainer) {
        List<SortingData> itemlist = new ArrayList<>();
        DankHandler handler = ((AbstractAbstractDankContainer) openContainer).getHandler();

        for (int i = 0; i < handler.getSlots(); i++) {
          ItemStack stack = handler.getStackInSlot(i);
          if (stack.isEmpty()) continue;
          boolean exists = SortingData.exists(itemlist, stack.copy());
          if (exists) {
            int rem = SortingData.addToList(itemlist, stack.copy());
            if (rem > 0) {
              ItemStack bigstack = stack.copy();
              bigstack.setCount(Integer.MAX_VALUE);
              ItemStack smallstack = stack.copy();
              smallstack.setCount(rem);
              itemlist.add(new SortingData(bigstack));
              itemlist.add(new SortingData(smallstack));
            }
          } else {
            itemlist.add(new SortingData(stack.copy()));
          }
        }
        handler.getContents().clear();
        Collections.sort(itemlist);
        for (SortingData data : itemlist) {
          ItemStack stack = data.stack.copy();
          ItemStack rem = stack.copy();
          for (int i = 0; i < handler.getSlots(); i++) {
            rem = handler.insertItem(i, rem, false);
            if (rem.isEmpty()) break;
          }
        }
      }
  }

  //todo config!
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

  public static int getSize(ItemStack bag){
    return bag.getOrCreateTag().getInt("Size");
  }

  public static void changeSlot(ItemStack bag, boolean right) {
    int selectedSlot = getSelectedSlot(bag);
    int size = getSize(bag);

    if (right) {
      selectedSlot++;
      if (selectedSlot >= size) selectedSlot = 0;
    } else {
      selectedSlot--;
      if (selectedSlot < 0) selectedSlot = size - 1;
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
    DankHandler handler = getHandler(bag,false);
    int selectedSlot = Utils.getSelectedSlot(bag);
    return handler.getStackInSlot(selectedSlot);
  }

  public static boolean doItemStacksShareWhitelistedTags(final ItemStack stack1,final ItemStack stack2) {
    if (stack1.hasTag() || stack2.hasTag()) return false;

    if (!stack1.getItem().isIn(WHITELISTED_TAGS) || !stack2.getItem().isIn(WHITELISTED_TAGS))return false;

    Set<ResourceLocation> taglist1 = stack1.getItem().getTags();
    Set<ResourceLocation> taglist2 = stack2.getItem().getTags();
    return !Collections.disjoint(taglist1,taglist2);
  }
}
