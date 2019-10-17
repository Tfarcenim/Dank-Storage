package com.tfar.dankstorage.inventory;

import com.tfar.dankstorage.utils.Utils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.IntStream;

public class DankHandler extends ItemStackHandler {

  public final int stacklimit;
  public int[] lockedSlots;

  public DankHandler(int size, int stacklimit) {
    super(size);
    this.stacklimit = stacklimit;
    lockedSlots = new int[size];
  }

  public boolean isEmpty(){
    return IntStream.range(0, this.getSlots()).allMatch(i -> this.getStackInSlot(i).isEmpty());
  }

  @Override
  public void onContentsChanged(int slot) {
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
  public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
    return !stack.getItem().isIn(Utils.BLACKLISTED_ITEMS);
  }

  @Override
  @Nonnull
  public ItemStack extractItem(int slot, int amount, boolean simulate) {
    if (amount == 0)
      return ItemStack.EMPTY;

    boolean isLocked = isLocked(slot);

    validateSlotIndex(slot);

    ItemStack existing = this.stacks.get(slot);

    if (existing.isEmpty())
      return ItemStack.EMPTY;

    int toExtract = Math.min(amount, stacklimit);
    //attempting to extract equal to or greater than what is present
    if (existing.getCount() <= toExtract) {
      if (!simulate) {
        //leave one behind
        if (isLocked)
        this.stacks.set(slot, ItemHandlerHelper.copyStackWithSize(existing,1));
        else//dont
          this.stacks.set(slot, ItemStack.EMPTY);
        onContentsChanged(slot);
      }

      if (isLocked){
        //return nothing
        if (existing.getCount() == 1)
        return ItemStack.EMPTY;
        else return ItemHandlerHelper.copyStackWithSize(existing, existing.getCount() - 1);
      } else

      return existing;
      //there is more than requested
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

  public boolean isLocked(int slot){
    return lockedSlots[slot] == 1;
  }

  @Override
  public CompoundNBT serializeNBT() {
    ListNBT nbtTagList = new ListNBT();
    for (int i = 0; i < getContents().size(); i++) {
      if (!getContents().get(i).isEmpty()) {
        int realCount = Math.min(stacklimit, getContents().get(i).getCount());
        CompoundNBT itemTag = new CompoundNBT();
        itemTag.putInt("Slot", i);
        getContents().get(i).write(itemTag);
        itemTag.putInt("ExtendedCount", realCount);
        nbtTagList.add(itemTag);
      }
    }
    CompoundNBT nbt = new CompoundNBT();
    nbt.put("Items", nbtTagList);
    nbt.putIntArray("LockedSlots",lockedSlots);
    nbt.putInt("Size", getContents().size());
    return nbt;
  }

  @Override
  public void deserializeNBT(CompoundNBT nbt) {
    setSize(nbt.contains("Size", Constants.NBT.TAG_INT) ? nbt.getInt("Size") : getContents().size());
    ListNBT tagList = nbt.getList("Items", Constants.NBT.TAG_COMPOUND);
    for (int i = 0; i < tagList.size(); i++) {
      CompoundNBT itemTags = tagList.getCompound(i);
      int slot = itemTags.getInt("Slot");

      if (slot >= 0 && slot < stacks.size()) {
        if (itemTags.contains("StackList", Constants.NBT.TAG_LIST)) {
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
    lockedSlots = nbt.getIntArray("LockedSlots").length == getSlots() ? nbt.getIntArray("LockedSlots") : new int[getSlots()];
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
