package tfar.dankstorage.inventory;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import tfar.dankstorage.utils.CommonUtils;
import tfar.dankstorage.utils.DankStats;
import tfar.dankstorage.utils.ItemStackWrapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public interface DankInterface extends ContainerData {

    String GHOST = "GhostItems";

    ItemStack getGhostItem(int slot);
    DankStats getDankStats();
    boolean frequencyLocked();
    void setItemDank(int slot,ItemStack stack);
    ItemStack getItemDank(int slot);

    NonNullList<ItemStack> getContents();
    NonNullList<ItemStack> getGhostItems();
    default int getFrequency() {
        return get(0);
    }

    int getContainerSizeDank();

    default void readGhostItems(ListTag listTag) {
        for (int i = 0; i < listTag.size(); i++) {
            CompoundTag itemTags = listTag.getCompound(i);
            int slot = itemTags.getInt("Slot");
            if (slot >= 0 && slot < getContainerSizeDank()) {
                if (itemTags.contains("StackList", Tag.TAG_LIST)) {
                    ItemStack stack = ItemStack.EMPTY;
                    ListTag stackTagList = itemTags.getList("StackList", Tag.TAG_COMPOUND);
                    for (int j = 0; j < stackTagList.size(); j++) {
                        CompoundTag itemTag = stackTagList.getCompound(j);
                        ItemStack temp = ItemStack.of(itemTag);
                        if (!temp.isEmpty()) {
                            if (stack.isEmpty()) stack = temp;
                            else stack.grow(temp.getCount());
                        }
                    }
                    if (!stack.isEmpty()) {
                        this.getGhostItems().set(slot, stack);
                    }
                } else {
                    ItemStack stack = ItemStack.of(itemTags);
                    this.getGhostItems().set(slot, stack);
                }
            }
        }
    }

    default void sort() {
        List<ItemStack> stacks = new ArrayList<>();

        for (ItemStack stack : getContents()) {
            if (!stack.isEmpty()) {
                CommonUtils.merge(stacks, stack.copy());
            }
        }

        List<ItemStackWrapper> wrappers = CommonUtils.wrap(stacks);

        Collections.sort(wrappers);

        for (int i = 0; i < getContainerSizeDank();i++) {
            setItemDank(i,ItemStack.EMPTY);
            getGhostItems().set(i,ItemStack.EMPTY);
        }

        //split up the stacks and add them to the slot

        int slotId = 0;

        for (int i = 0; i < wrappers.size(); i++) {
            ItemStack stack = wrappers.get(i).stack;
            int count = stack.getCount();
            DankStats dankStats = getDankStats();
            if (count > dankStats.stacklimit) {
                int fullStacks = count / dankStats.stacklimit;
                int partialStack = count - fullStacks * dankStats.stacklimit;

                for (int j = 0; j < fullStacks;j++) {
                    setItemDank(slotId, CommonUtils.copyStackWithSize(stack, dankStats.stacklimit));
                    slotId++;
                }
                if (partialStack > 0) {
                    setItemDank(slotId,  CommonUtils.copyStackWithSize(stack, partialStack));
                    slotId++;
                }
            } else {
                setItemDank(slotId,stack);
                slotId++;
            }
        }
    }


    default void readItems(ListTag listTag) {
        for (int i = 0; i < listTag.size(); i++) {
            CompoundTag itemTags = listTag.getCompound(i);
            int slot = itemTags.getInt("Slot");
            if (slot >= 0 && slot < getContainerSizeDank()) {
                if (itemTags.contains("StackList", Tag.TAG_LIST)) {
                    ItemStack stack = ItemStack.EMPTY;
                    ListTag stackTagList = itemTags.getList("StackList", Tag.TAG_COMPOUND);
                    for (int j = 0; j < stackTagList.size(); j++) {
                        CompoundTag itemTag = stackTagList.getCompound(j);
                        ItemStack temp = ItemStack.of(itemTag);
                        if (!temp.isEmpty()) {
                            if (stack.isEmpty()) stack = temp;
                            else stack.grow(temp.getCount());
                        }
                    }
                    if (!stack.isEmpty()) {
                        int count = stack.getCount();
                        count = Math.min(count, getMaxStackSizeDank());
                        stack.setCount(count);

                        this.setItemDank(slot, stack);
                    }
                } else {
                    ItemStack stack = ItemStack.of(itemTags);
                    if (itemTags.contains("ExtendedCount", Tag.TAG_INT)) {
                        stack.setCount(itemTags.getInt("ExtendedCount"));
                    }
                    this.setItemDank(slot, stack);
                }
            }
        }
    }

    int getMaxStackSizeDank();

    default void validate() {
        int containerSizeDank = getContainerSizeDank();
        if (getDankStats() == DankStats.zero) {
            throw new RuntimeException("dank has no stats?");
        } else if (containerSizeDank == 0) {
            throw new RuntimeException("dank is empty?");
        } else {
            if (getGhostItems().size() != containerSizeDank) {
                throw new RuntimeException("inequal size");
            }
        }
    }

    default boolean valid() {
        return getDankStats() != DankStats.zero;
    }


}
