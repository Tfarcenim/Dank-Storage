package com.tfar.dankstorage.capability;

import com.tfar.dankstorage.inventory.DankHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.util.Constants;

public class CapabilityDankStorage {
  @CapabilityInject(DankHandler.class)
  public static Capability<DankHandler> DANK_STORAGE_CAPABILITY = null;

  public static void register() {
    CapabilityManager.INSTANCE.register(DankHandler.class, new Capability.IStorage<DankHandler>() {
      @Override
      public INBT writeNBT(Capability<DankHandler> cap, DankHandler instance, Direction side) {
        ListNBT nbtTagList = new ListNBT();
        for (int i = 0; i < instance.getContents().size(); i++) {
          if (!instance.getContents().get(i).isEmpty()) {
            int realCount = Math.min(instance.stacklimit, instance.getContents().get(i).getCount());
            CompoundNBT itemTag = new CompoundNBT();
            itemTag.putInt("Slot", i);
            instance.getContents().get(i).write(itemTag);
            itemTag.putInt("ExtendedCount", realCount);
            nbtTagList.add(itemTag);
          }
        }
        CompoundNBT nbt = new CompoundNBT();
        nbt.put("Items", nbtTagList);
        nbt.putInt("Size", instance.getContents().size());
        return nbt;
      }

      @Override
      public void readNBT(Capability<DankHandler> capability, DankHandler instance, Direction side, INBT base) {
        CompoundNBT nbt = (CompoundNBT)base;
        instance.setSize(nbt.contains("Size", Constants.NBT.TAG_INT) ? nbt.getInt("Size") : instance.getContents().size());
        ListNBT tagList = nbt.getList("Items", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < tagList.size(); i++) {
          CompoundNBT itemTags = tagList.getCompound(i);
          int slot = itemTags.getInt("Slot");

          if (slot >= 0 && slot < instance.getContents().size()) {
            if (itemTags.contains("StackList", Constants.NBT.TAG_LIST)) { // migrate from old ExtendedItemStack system
              ItemStack stack = ItemStack.EMPTY;
              ListNBT stackTagList = itemTags.getList("StackList", Constants.NBT.TAG_COMPOUND);
              for (int j = 0; j < stackTagList.size(); j++) {
                CompoundNBT itemTag = stackTagList.getCompound(j);
                ItemStack temp = ItemStack.read(itemTag);
                if (!temp.isEmpty()) {
                  if (stack.isEmpty()) stack = temp;
                  else stack.grow(temp.getCount());
                }
              }
              if (!stack.isEmpty()) {
                int count = stack.getCount();
                count = Math.min(count, instance.getStackLimit(slot, stack));
                stack.setCount(count);

                instance.getContents().set(slot, stack);
              }
            } else {
              ItemStack stack = ItemStack.read(itemTags);
              if (itemTags.contains("ExtendedCount", Constants.NBT.TAG_INT)) {
                stack.setCount(itemTags.getInt("ExtendedCount"));
              }
              instance.getContents().set(slot, stack);
            }
          }
        }
        //instance.onLoad();
      }
    }, () -> null);
  }
}
