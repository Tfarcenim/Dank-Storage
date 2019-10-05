package com.tfar.dankstorage.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import java.util.stream.IntStream;

public class DankHandler extends ItemStackHandler {

  public final int stacklimit;

  public DankHandler(int size, int stacklimit) {
    super(size);
    this.stacklimit = stacklimit;
  }

  public boolean isEmpty(){
    return IntStream.range(0, this.getSlots()).allMatch(i -> this.getStackInSlot(i).isEmpty());
  }

  public void clear(){
    this.stacks.clear();
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
    //todo might break mods, but removing this causes a dupe bug
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
  public CompoundNBT serializeNBT() {
    ListNBT nbtTagList = new ListNBT();
    for (int i = 0; i < stacks.size(); i++) {
      if (!stacks.get(i).isEmpty()) {
        int realCount = Math.min(stacklimit, stacks.get(i).getCount());
        CompoundNBT itemTag = new CompoundNBT();
        itemTag.putInt("Slot", i);
        stacks.get(i).write(itemTag);
        itemTag.putInt("ExtendedCount", realCount);
        nbtTagList.add(itemTag);
      }
    }
    CompoundNBT nbt = new CompoundNBT();
    nbt.put("Items", nbtTagList);
    nbt.putInt("Size", stacks.size());
    return nbt;
  }

  @Override
  public void deserializeNBT(CompoundNBT nbt) {
    setSize(nbt.contains("Size", Constants.NBT.TAG_INT) ? nbt.getInt("Size") : stacks.size());
    ListNBT tagList = nbt.getList("Items", Constants.NBT.TAG_COMPOUND);
    for (int i = 0; i < tagList.size(); i++) {
      CompoundNBT itemTags = tagList.getCompound(i);
      int slot = itemTags.getInt("Slot");

      if (slot >= 0 && slot < stacks.size()) {
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
            count = Math.min(count, getStackLimit(slot, stack));
            stack.setCount(count);

            stacks.set(slot, stack);
          }
        } else {
          ItemStack stack = ItemStack.read(itemTags);
          if (itemTags.contains("ExtendedCount", Constants.NBT.TAG_INT)) {
            stack.setCount(itemTags.getInt("ExtendedCount"));
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
