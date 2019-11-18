package com.tfar.dankstorage.utils;

import com.tfar.dankstorage.DankStorage;
import com.tfar.dankstorage.block.DankBlock;
import com.tfar.dankstorage.block.DankItemBlock;
import com.tfar.dankstorage.container.AbstractAbstractDankContainer;
import com.tfar.dankstorage.inventory.DankHandler;
import com.tfar.dankstorage.inventory.PortableDankHandler;
import com.tfar.dankstorage.network.CMessageToggleUseType;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.EncoderException;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.tfar.dankstorage.network.CMessageTogglePickup.*;
import static com.tfar.dankstorage.network.CMessageToggleUseType.useTypes;

public class Utils {

  public static final Tag<Item> WHITELISTED_TAGS = new ItemTags.Wrapper(new ResourceLocation(DankStorage.MODID,"whitelisted_tags"));
  public static final Tag<Item> BLACKLISTED_STORAGE = new ItemTags.Wrapper(new ResourceLocation(DankStorage.MODID,"blacklisted_storage"));
  public static final Tag<Item> BLACKLISTED_USAGE = new ItemTags.Wrapper(new ResourceLocation(DankStorage.MODID,"blacklisted_usage"));

  public static final Tag<Item> WRENCHES = new ItemTags.Wrapper(new ResourceLocation("forge","wrenches"));

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

  public static int getStackLimit(ItemStack bag) {
    return getStackLimit(bag.getItem().getRegistryName());
  }

  public static int getStackLimit(ResourceLocation registryname) {
    switch (getTier(registryname)) {
      case 1:
      default:
        return DankStorage.ServerConfig.stacklimit1.get();
      case 2:
        return DankStorage.ServerConfig.stacklimit2.get();
      case 3:
        return DankStorage.ServerConfig.stacklimit3.get();
      case 4:
        return DankStorage.ServerConfig.stacklimit4.get();
      case 5:
        return DankStorage.ServerConfig.stacklimit5.get();
      case 6:
        return DankStorage.ServerConfig.stacklimit6.get();
      case 7:
        return DankStorage.ServerConfig.stacklimit7.get();
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
    PortableDankHandler handler = getHandler(bag,false);
    //don't change slot if empty
    if (handler.noValidSlots())return;
    int selectedSlot = getSelectedSlot(bag) + (right ? 1 : -1);
    int size = getSize(bag);
    if (selectedSlot < 0)selectedSlot = size - 1;
    //keep iterating until a valid slot is found (not empty and not blacklisted from usage)
    while (handler.getStackInSlot(selectedSlot).isEmpty() || handler.getStackInSlot(selectedSlot).getItem().isIn(BLACKLISTED_USAGE)) {
      if (right) {
        selectedSlot++;
        if (selectedSlot >= size) selectedSlot = 0;
      } else {
        selectedSlot--;
        if (selectedSlot < 0) selectedSlot = size - 1;
      }
    }
    setSelectedSlot(bag, selectedSlot);
  }

  public static boolean tag(ItemStack bag) {
    return bag.getItem() instanceof DankItemBlock && bag.hasTag() && bag.getTag().getBoolean("tag");
  }

  public static PortableDankHandler getHandler(ItemStack bag, boolean manual) {
    return new PortableDankHandler(bag, manual);
  }

  public static int getNbtSize(ItemStack stack){
    return getNbtSize(stack.getTag());
  }

  public static DankBlock getBlockFromTier(int tier){
    return (DankBlock) ForgeRegistries.BLOCKS.getValue(new ResourceLocation(DankStorage.MODID,"dank_"+tier));
  }

  public static int getNbtSize(@Nullable CompoundNBT nbt){
    PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
    buffer.writeCompoundTag(nbt);
    buffer.release();
    return buffer.writerIndex();
  }

  public static ItemStack getItemStackInSelectedSlot(ItemStack bag){
    DankHandler handler = (DankHandler) bag.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(null);
    int selectedSlot = Utils.getSelectedSlot(bag);
    ItemStack stack = handler.getStackInSlot(selectedSlot);
    return stack.getItem().isIn(BLACKLISTED_USAGE) ? ItemStack.EMPTY : stack;
  }

  public static boolean doItemStacksShareWhitelistedTags(final ItemStack stack1,final ItemStack stack2) {
    if (stack1.hasTag() || stack2.hasTag()) return false;

    if (!stack1.getItem().isIn(WHITELISTED_TAGS) || !stack2.getItem().isIn(WHITELISTED_TAGS))return false;

    Set<ResourceLocation> taglist1 = stack1.getItem().getTags();
    Set<ResourceLocation> taglist2 = stack2.getItem().getTags();
    return !Collections.disjoint(taglist1,taglist2);
  }


  public static ItemStack readExtendedItemStack(ByteBuf buf){
    int i = buf.readInt();

    if (i < 0) {
      return ItemStack.EMPTY;
    } else {
      int j = buf.readInt();
      ItemStack itemstack = new ItemStack(Item.getItemById(i), j);
      itemstack.setTag(readNBT(buf));
      return itemstack;
    }
  }

  public static void writeExtendedItemStack(ByteBuf buf, ItemStack stack) {
    if (stack.isEmpty()) {
      buf.writeInt(-1);
    } else {
      buf.writeInt(Item.getIdFromItem(stack.getItem()));
      buf.writeInt(stack.getCount());
      CompoundNBT nbttagcompound = null;

      if (stack.getItem().getShareTag(stack) != null) {
        nbttagcompound = stack.getItem().getShareTag(stack);
      }

      writeNBT(buf, nbttagcompound);
    }
  }

  public static void writeNBT(ByteBuf buf, @Nullable CompoundNBT nbt) {
    if (nbt == null) {
      buf.writeByte(0);
    } else {
      try {
        CompressedStreamTools.write(nbt, new ByteBufOutputStream(buf));
      } catch (IOException ioexception) {
        throw new EncoderException(ioexception);
      }
    }
  }

  public static CompoundNBT readNBT(ByteBuf buf) {
    int i = buf.readerIndex();
    byte b0 = buf.readByte();

    if (b0 == 0) {
      return null;
    } else {
      buf.readerIndex(i);
      try {
        return CompressedStreamTools.read(new ByteBufInputStream(buf), new NBTSizeTracker(2097152L));
      } catch (IOException ioexception) {
        throw new EncoderException(ioexception);
      }
    }
  }
}
