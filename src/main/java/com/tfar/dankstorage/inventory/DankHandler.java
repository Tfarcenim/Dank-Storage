package com.tfar.dankstorage.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import java.util.stream.IntStream;

public class DankHandler extends ItemStackHandler {

  public final int stacklimit;

  protected int selectedSlot;

  public DankHandler(int size, int stacklimit) {
    super(size);
    this.stacklimit = stacklimit;
  }

  public boolean isEmpty(){
    return IntStream.range(0, this.getSlots()).allMatch(i -> this.getStackInSlot(i).isEmpty());
  }

  @Override
  public int getSlotLimit(int slot) {
    return stacklimit;
  }

  @Override
  public int getStackLimit(int slot, @Nonnull ItemStack stack) {
    return stacklimit;
  }

  @Override
  public void onContentsChanged(int slot) {

  }

  @Override
  @Nonnull
  public ItemStack extractItem(int slot, int amount, boolean simulate) {
    if (amount == 0)
      return ItemStack.EMPTY;

    validateSlotIndex(slot);

    ItemStack existing = this.stacks.get(slot);

    if (existing.isEmpty())
      return ItemStack.EMPTY;

    int toExtract = Math.min(amount, stacklimit);

    if (existing.getMaxStackSize() == 1)toExtract = 1;

    if (existing.getCount() <= toExtract) {
      if (!simulate) {
        this.stacks.set(slot, ItemStack.EMPTY);
        onContentsChanged(slot);
      }
      return existing;
    } else {
      if (!simulate) {
        this.stacks.set(slot, ItemHandlerHelper.copyStackWithSize(existing, existing.getCount() - toExtract));
        onContentsChanged(slot);
      }

      return ItemHandlerHelper.copyStackWithSize(existing, toExtract);
    }
  }

  public NonNullList<ItemStack> getContents(){
    return stacks;
  }

  @Override
  public NBTTagCompound serializeNBT() {
    NBTTagList nbtTagList = new NBTTagList();
    for (int i = 0; i < stacks.size(); i++) {
      if (!stacks.get(i).isEmpty()) {
        int realCount = Math.min(stacklimit, stacks.get(i).getCount());
        NBTTagCompound itemTag = new NBTTagCompound();
        itemTag.setInteger("Slot", i);
        stacks.get(i).writeToNBT(itemTag);
        itemTag.setInteger("ExtendedCount", realCount);
        nbtTagList.appendTag(itemTag);
      }
    }
    NBTTagCompound nbt = new NBTTagCompound();
    nbt.setTag("Items", nbtTagList);
    nbt.setInteger("Size", stacks.size());
    return nbt;
  }

  @Override
  public void deserializeNBT(NBTTagCompound nbt) {
    setSize(nbt.hasKey("Size", Constants.NBT.TAG_INT) ? nbt.getInteger("Size") : stacks.size());
    NBTTagList tagList = nbt.getTagList("Items", Constants.NBT.TAG_COMPOUND);
    for (int i = 0; i < tagList.tagCount(); i++) {
      NBTTagCompound itemTags = tagList.getCompoundTagAt(i);
      int slot = itemTags.getInteger("Slot");

      if (slot >= 0 && slot < stacks.size()) {
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
            count = Math.min(count, getStackLimit(slot, stack));
            stack.setCount(count);

            stacks.set(slot, stack);
          }
        } else {
          ItemStack stack = new ItemStack(itemTags);
          if (itemTags.hasKey("ExtendedCount", Constants.NBT.TAG_INT)) {
            stack.setCount(itemTags.getInteger("ExtendedCount"));
          }
          stacks.set(slot, stack);
        }
      }
    }
    onLoad();
  }

  public int calcRedstone() {
    int numStacks = 0;
    float f = 0F;

    for (int slot = 0; slot < this.getSlots(); slot++) {
      ItemStack stack = this.getStackInSlot(slot);

      if (!stack.isEmpty()) {
        f += (float) stack.getCount() / (float) this.getStackLimit(slot, stack);
        numStacks++;
      }
    }

    f /= this.getSlots();
    return MathHelper.floor(f * 14F) + (numStacks > 0 ? 1 : 0);
  }
}
