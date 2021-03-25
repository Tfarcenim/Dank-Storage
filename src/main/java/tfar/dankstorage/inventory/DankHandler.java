package tfar.dankstorage.inventory;

import net.minecraft.entity.player.PlayerEntity;
import tfar.dankstorage.ModTags;
import tfar.dankstorage.utils.DankStats;
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

  public int[] lockedSlots;
  protected DankStats stats;

  public DankHandler(DankStats stats) {
    super(stats.slots);
    lockedSlots = new int[stats.slots];
    this.stats = stats;
  }

  public void setStats(DankStats stats) {
    this.stats = stats;
    setSize(stats.slots);
  }

  //overridden to prevent voiding, DO NOT CALL FROM THE OUTSIDE
  @Override
  public void setSize(int size) {
    NonNullList<ItemStack> newStacks = NonNullList.withSize(size,ItemStack.EMPTY);
    int[] newLocked = new int[size];
    for (int i = 0; i < stacks.size(); i++) {
      ItemStack stack = stacks.get(i);
      if (i < size) {
        newStacks.set(i, stack);
        newLocked[i] = lockedSlots[i];
      }
    }
    stacks = newStacks;
    lockedSlots = newLocked;
  }

  public boolean isEmpty() {
    return IntStream.range(0, this.getSlots()).allMatch(i -> this.getStackInSlot(i).isEmpty());
  }

  public boolean noValidSlots() {
    return IntStream.range(0,getSlots())
            .mapToObj(this::getStackInSlot)
            .allMatch(stack -> stack.isEmpty() || stack.getItem().isIn(ModTags.BLACKLISTED_USAGE));
  }

  @Override
  public void onContentsChanged(int slot) {
  }

  @Override
  public int getSlotLimit(int slot) {
    return stats.stacklimit;
  }

  @Override
  public int getStackLimit(int slot, @Nonnull ItemStack stack) {
    return stats.stacklimit;
  }

  @Override
  public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
    return !stack.getItem().isIn(ModTags.BLACKLISTED_STORAGE);
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

    int toExtract = Math.min(amount, stats.stacklimit);
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

  public void lockSlot(int slot) {
    lockedSlots[slot] = 1 - lockedSlots[slot];
  }

  public boolean isLocked(int slot) {
    return lockedSlots[slot] == 1;
  }

  @Override
  public CompoundNBT serializeNBT() {
    ListNBT nbtTagList = new ListNBT();
    for (int i = 0; i < getContents().size(); i++) {
      if (!getContents().get(i).isEmpty()) {
        int realCount = Math.min(stats.stacklimit, getContents().get(i).getCount());
        CompoundNBT itemTag = new CompoundNBT();
        itemTag.putInt("Slot", i);
        getContents().get(i).write(itemTag);
        itemTag.putInt("ExtendedCount", realCount);
        nbtTagList.add(itemTag);
      }
    }
    CompoundNBT nbt = new CompoundNBT();
    nbt.put("Items", nbtTagList);
    if (lockedSlots.length > 0)
    nbt.putIntArray("LockedSlots",lockedSlots);
    return nbt;
  }

  @Override
  public void deserializeNBT(CompoundNBT nbt) {
    lockedSlots = nbt.getIntArray("LockedSlots");
    if (lockedSlots.length < stats.slots) {
      lockedSlots = new int[stats.slots];
    }
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
    onLoad();
  }

  @Nonnull
  @Override
  public ItemStack getStackInSlot(int slot) {
    return super.getStackInSlot(slot);
  }

  public boolean canPlayerUse(PlayerEntity player) {
    return false;
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

  public void onOpen(PlayerEntity player) {

  }

  public void onClose(PlayerEntity player) {

  }

}
