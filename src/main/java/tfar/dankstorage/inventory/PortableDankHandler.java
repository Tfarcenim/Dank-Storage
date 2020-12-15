package tfar.dankstorage.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Hand;
import net.minecraftforge.common.util.Constants;
import tfar.dankstorage.utils.DankStats;
import tfar.dankstorage.utils.Utils;
import net.minecraft.item.ItemStack;

public class PortableDankHandler extends DankHandler {

  public final ItemStack bag;
  private final Hand hand;

  public PortableDankHandler(ItemStack bag, Hand hand) {
    this(Utils.getStats(bag),bag,hand);
  }

  protected PortableDankHandler(DankStats stats, ItemStack bag,Hand hand) {
    super(stats);
    this.bag = bag;
    this.hand = hand;
    load();
  }

  public void save() {
      bag.getOrCreateTag().put(Utils.INV,serializeNBT());
  }

  public void load() {
      deserializeNBT(bag.getOrCreateChildTag(Utils.INV));
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
    return nbt;
  }

  @Override
  public void deserializeNBT(CompoundNBT nbt) {
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

  @Override
  public boolean canPlayerUse(PlayerEntity player) {
    return player.getHeldItem(hand) == bag;
  }

  @Override
  public void onContentsChanged(int slot) {
    this.save();
  }
}
