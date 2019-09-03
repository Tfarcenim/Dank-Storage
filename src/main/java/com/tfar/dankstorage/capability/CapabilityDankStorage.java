package com.tfar.dankstorage.capability;

import com.tfar.dankstorage.inventory.DankHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
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
      public NBTBase writeNBT(Capability<DankHandler> cap, DankHandler instance, EnumFacing side) {
        NBTTagList nbtTagList = new NBTTagList();
        for (int i = 0; i < instance.getContents().size(); i++) {
          if (!instance.getContents().get(i).isEmpty()) {
            int realCount = Math.min(instance.stacklimit, instance.getContents().get(i).getCount());
            NBTTagCompound itemTag = new NBTTagCompound();
            itemTag.setInteger("Slot", i);
            instance.getContents().get(i).writeToNBT(itemTag);
            itemTag.setInteger("ExtendedCount", realCount);
            nbtTagList.appendTag(itemTag);
          }
        }
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setTag("Items", nbtTagList);
        nbt.setInteger("Size", instance.getContents().size());
        return nbt;
      }

      @Override
      public void readNBT(Capability<DankHandler> capability, DankHandler instance, EnumFacing side, NBTBase base) {
        NBTTagCompound nbt = (NBTTagCompound)base;
        instance.setSize(nbt.hasKey("Size", Constants.NBT.TAG_INT) ? nbt.getInteger("Size") : instance.getContents().size());
        NBTTagList tagList = nbt.getTagList("Items", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < tagList.tagCount(); i++) {
          NBTTagCompound itemTags = tagList.getCompoundTagAt(i);
          int slot = itemTags.getInteger("Slot");

          if (slot >= 0 && slot < instance.getContents().size()) {
            if (itemTags.hasKey("StackList", Constants.NBT.TAG_LIST)) { // migrate from old ExtendedItemStack system
              ItemStack stack = ItemStack.EMPTY;
              NBTTagList stackTagList = itemTags.getTagList("StackList", Constants.NBT.TAG_COMPOUND);
              for (int j = 0; j < stackTagList.tagCount(); j++) {
                NBTTagCompound itemTag = stackTagList.getCompoundTagAt(j);
                ItemStack temp = new ItemStack(itemTag);
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
              ItemStack stack = new ItemStack(itemTags);
              if (itemTags.hasKey("ExtendedCount", Constants.NBT.TAG_INT)) {
                stack.setCount(itemTags.getInteger("ExtendedCount"));
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
