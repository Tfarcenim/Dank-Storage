package tfar.dankstorage.inventory;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import tfar.dankstorage.ModTags;
import tfar.dankstorage.platform.Services;
import tfar.dankstorage.utils.CommonUtils;
import tfar.dankstorage.utils.DankStats;
import tfar.dankstorage.utils.SerializationHelper;
import tfar.dankstorage.world.DankSavedData;

import java.util.*;
import java.util.stream.IntStream;

public abstract class DankInventory implements ContainerData {

    private final DankSavedData data;
    protected static String GHOST = "GhostItems";
    public NonNullList<ItemStack> items;
    protected NonNullList<ItemStack> ghostItems;
    public int capacity;
    private boolean frequencyLocked = true;
    protected int textColor = -1;


    public static final int TXT_COLOR = 0;
    public static final int FREQ_LOCK = 1;
    private SortingType sortingType = SortingType.descending;
    private boolean autoSort = true;

    public DankInventory(DankStats stats, DankSavedData data) {
        this(stats.slots,stats.stacklimit,data);
    }

    public DankInventory(int slots, int capacity, @Nullable DankSavedData data) {
        items = NonNullList.withSize(slots, ItemStack.EMPTY);
        ghostItems = NonNullList.withSize(slots,ItemStack.EMPTY);
        this.capacity = capacity;
        this.data = data;
    }

    public static DankInventory createDummy(DankStats dankStats) {
        return Services.PLATFORM.createInventory(dankStats,null);
    }


    public void setSortingType(SortingType sortingType) {
        this.sortingType = sortingType;
        setDirty();
    }

    public SortingType getSortingType() {
        return sortingType;
    }

    public ItemStack getGhostItem(int slot) {
        return getGhostItems().get(slot);
    }

    public boolean frequencyLocked() {
        return frequencyLocked;
    }

    public int textColor() {
        return get(TXT_COLOR);
    }

    public void setTextColor(int color) {
        set(TXT_COLOR, color);
    }

    public void toggleFrequencyLock() {
        frequencyLocked = !frequencyLocked;
        setDirty();
    }

    public void setItemDank(int slot, ItemStack stack) {
        this.items.set(slot, stack);
    }

    public ItemStack getItemDank(int slot) {
        return items.get(slot);
    }

    public boolean canPlaceItem(int slot, ItemStack stack) {
        boolean checkGhostItem = !hasGhostItem(slot) || getGhostItem(slot).getItem() == stack.getItem();
        return !stack.is(ModTags.BLACKLISTED_STORAGE)
                && checkGhostItem;
    }

    public void setGhostItem(int slot, Item item) {
        getGhostItems().set(slot, new ItemStack(item));
    }

    public NonNullList<ItemStack> getContents() {
        return items;
    }
    public void setItemsDank(NonNullList<ItemStack> stacks) {
        this.items = stacks;
    }

    public NonNullList<ItemStack> getGhostItems() {
        return ghostItems;
    }

    public void setGhostItems(NonNullList<ItemStack> ghostItems) {
        this.ghostItems = ghostItems;
    }

    @Override
    public int get(int slot) {
        return switch (slot) {
            case TXT_COLOR -> textColor;
            case FREQ_LOCK -> frequencyLocked ? 1 : 0;
            default -> AbstractContainerMenu.SLOT_CLICKED_OUTSIDE;
        };
    }

    @Override
    public void set(int slot, int value) {
        switch (slot) {
            case TXT_COLOR -> textColor = value;
            case FREQ_LOCK -> frequencyLocked = value == 1;
        }
        setDirty();
    }

    //0 is the id, 1 is text color, and 2 is frequency lock
    @Override
    public int getCount() {
        return 2;
    }

    ItemStack addItemDank(int slot, ItemStack stack) {
        return insertStack(slot,stack,false);
    }



    public ItemStack insertStack(int slot, ItemStack stack, boolean simulate) {
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        } else if (!this.canPlaceItem(slot, stack)) {
            return stack;
        } else {
           // this.validateSlotIndex(slot);
            ItemStack existing = this.items.get(slot);
            int limit = this.getMaxStackSizeSensitive(stack);
            if (!existing.isEmpty()) {
                if (!ItemStack.isSameItemSameComponents(stack, existing)) {
                    return stack;
                }

                limit -= existing.getCount();
            }

            if (limit <= 0) {
                return stack;
            } else {
                boolean reachedLimit = stack.getCount() > limit;
                if (!simulate) {
                    if (existing.isEmpty()) {
                        this.items.set(slot, reachedLimit ? stack.copyWithCount(limit) : stack);
                    } else {
                        existing.grow(reachedLimit ? limit : stack.getCount());
                    }
                    setDirty();
                    //this.onContentsChanged(slot);
                }

                return reachedLimit ? stack.copyWithCount(stack.getCount() - limit) : ItemStack.EMPTY;
            }
        }
    }

    public ItemStack extractStack(int slot, int amount, boolean simulate) {
        if (amount == 0) {
            return ItemStack.EMPTY;
        } else {
           // this.validateSlotIndex(slot);
            ItemStack existing = this.items.get(slot);
            if (existing.isEmpty()) {
                return ItemStack.EMPTY;
            } else {
                int toExtract = Math.min(amount, existing.getMaxStackSize());
                if (existing.getCount() <= toExtract) {
                    if (!simulate) {
                        this.items.set(slot, ItemStack.EMPTY);
                        setDirty();
                        //this.onContentsChanged(slot);
                        return existing;
                    } else {
                        return existing.copy();
                    }
                } else {
                    if (!simulate) {
                        this.items.set(slot, existing.copyWithCount(existing.getCount() - toExtract));
                        setDirty();
                        //this.onContentsChanged(slot);
                    }

                    return existing.copyWithCount(toExtract);
                }
            }
        }
    }

    public ItemStack extractStackTarget(int amount, boolean simulate, ItemStack target) {
        if (amount == 0 || target.isEmpty()) {
            return ItemStack.EMPTY;
        } else {
            int remaining = amount;
            int extracted = 0;
            for (int i = 0; i < slotCount();i++) {
                ItemStack slotStack = getItemDank(i);
                if (ItemStack.isSameItemSameComponents(target,slotStack)) {
                    ItemStack stack = extractStack(i, remaining, simulate);
                    remaining -= stack.getCount();
                    extracted += stack.getCount();
                    if (remaining <= 0) {
                        break;
                    }
                }
            }
            return target.copyWithCount(extracted);
        }
    }

     void readGhostItems(HolderLookup.Provider provider, ListTag listTag) {
        for (int i = 0; i < listTag.size(); i++) {
            CompoundTag itemTags = listTag.getCompound(i);
            int slot = itemTags.getInt("Slot");
            if (slot >= 0 && slot < slotCount()) {
                if (itemTags.contains("StackList", Tag.TAG_LIST)) {
                    ItemStack stack = ItemStack.EMPTY;
                    ListTag stackTagList = itemTags.getList("StackList", Tag.TAG_COMPOUND);
                    for (int j = 0; j < stackTagList.size(); j++) {
                        CompoundTag itemTag = stackTagList.getCompound(j);
                        ItemStack temp = ItemStack.parseOptional(provider,itemTag);
                        if (!temp.isEmpty()) {
                            if (stack.isEmpty()) stack = temp;
                            else stack.grow(temp.getCount());
                        }
                    }
                    if (!stack.isEmpty()) {
                        this.getGhostItems().set(slot, stack);
                    }
                } else {
                    ItemStack stack = ItemStack.parseOptional(provider,itemTags);
                    this.getGhostItems().set(slot, stack);
                }
            }
        }
    }

    public int getMaxStackSizeSensitive(ItemStack stack) {
        return stack.is(ModTags.UNSTACKABLE) ? 1 : getMaxStackSizeDank();
    }

    public void sort() {
        List<ItemStack> gathered = new ArrayList<>();
        Set<ItemStack> lockedItems = new HashSet<>();

        for (int i = 0; i < items.size(); i++) {
            ItemStack stack = items.get(i);
            ItemStack ghost = ghostItems.get(i);
            if (!stack.isEmpty()) {
                CommonUtils.merge(gathered, stack.copy());
                boolean unique = true;
                for (ItemStack stack1 : lockedItems) {
                    if (ItemStack.isSameItemSameComponents(stack1, stack)) {
                        unique = false;
                        break;
                    }
                }
                if (unique && !ghost.isEmpty()) {
                    lockedItems.add(stack.copyWithCount(1));
                }
            }
        }

        gathered.sort(sortingType.comparator);

        for (int i = 0; i < slotCount(); i++) {
            items.set(i, ItemStack.EMPTY);
            ghostItems.set(i, ItemStack.EMPTY);
        }
        //split up the gathered and add them to the slot
        int slotId = 0;

        for (int i = 0; i < gathered.size(); i++) {
            ItemStack stack = gathered.get(i);
            int count = stack.getCount();

            int tankSize = capacity;

            if (count > tankSize) {
                int fullStacks = count / tankSize;
                int partialStack = count - fullStacks * tankSize;

                for (int j = 0; j < fullStacks; j++) {
                    items.set(slotId, stack.copyWithCount(tankSize));

                    if (anyMatch(stack,lockedItems)) {
                        ghostItems.set(slotId,stack);
                    }

                    slotId++;
                }
                if (partialStack > 0) {
                    items.set(slotId, stack.copyWithCount(partialStack));

                    if (anyMatch(stack,lockedItems)) {
                        ghostItems.set(slotId,stack);
                    }

                    slotId++;
                }
            } else {
                items.set(slotId, stack);
                if (anyMatch(stack,lockedItems)) {
                    ghostItems.set(slotId,stack);
                }

                slotId++;
            }
        }
    }

    static boolean anyMatch(ItemStack stack, Set<ItemStack> stacks) {
        for (ItemStack stack1 : stacks) {
            if (ItemStack.isSameItemSameComponents(stack1,stack)) {
                return true;
            }
        }
        return false;
    }

    public void compress(ServerPlayer player) {
        sort();
        ServerLevel level = player.serverLevel();
        List<ItemStack> addLater = new ArrayList<>();
        for (int i = 0; i < getMaxStackSizeDank(); i++) {
            ItemStack stack = getItemDank(i);
            if (stack.isEmpty()) {
                break;
            }
            if (CommonUtils.canCompress(level, stack)) {
                Pair<ItemStack, Integer> result = CommonUtils.compress(stack, player.serverLevel().registryAccess());
                ItemStack resultStack = result.getFirst();
                if (!resultStack.isEmpty()) {
                    int division = result.getSecond();
                    int compressedCount = stack.getCount() / division;
                    int remainderCount = stack.getCount() % division;
                    setItemDank(i, CommonUtils.copyStackWithSize(resultStack, compressedCount));
                    addLater.add(CommonUtils.copyStackWithSize(stack, remainderCount));
                }
            }
        }
        sort();

        for (ItemStack itemStack : addLater) {
            ItemStack remainder = itemStack.copy();
            for (int i = 0; i < slotCount(); i++) {
                remainder = addItemDank(i, remainder);
                if (remainder.isEmpty()) break;
            }
            if (!remainder.isEmpty()) {
                player.addItem(remainder);
                if (!remainder.isEmpty()) {
                    player.drop(remainder, false);
                }
            }
        }
        sort();
    }


     void readItems(HolderLookup.Provider provider,ListTag listTag) {
        for (int i = 0; i < listTag.size(); i++) {
            CompoundTag itemTags = listTag.getCompound(i);
            int slot = itemTags.getInt("Slot");
            if (slot >= 0 && slot < slotCount()) {
                if (itemTags.contains("StackList", Tag.TAG_LIST)) {
                    ItemStack stack = ItemStack.EMPTY;
                    ListTag stackTagList = itemTags.getList("StackList", Tag.TAG_COMPOUND);
                    for (int j = 0; j < stackTagList.size(); j++) {
                        CompoundTag itemTag = stackTagList.getCompound(j);
                        ItemStack temp = SerializationHelper.decodeLargeItemStack(provider,itemTag);
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
                    ItemStack stack = SerializationHelper.decodeLargeItemStack(provider,itemTags);
                    this.setItemDank(slot, stack);
                }
            }
        }
    }

    public CompoundTag save(HolderLookup.Provider provider) {
        ListTag nbtTagList = new ListTag();
        for (int i = 0; i < this.getContents().size(); i++) {
            ItemStack stack = getContents().get(i);
            if (!stack.isEmpty()) {
                CompoundTag itemTag = new CompoundTag();
                //getContents().get(i).save(provider,itemTag);
                itemTag.putInt("Slot", i);
                nbtTagList.add(SerializationHelper.encodeLargeStack(stack, provider, itemTag));
            }
        }


        ListTag ghostItemNBT = new ListTag();
        for (int i = 0; i < this.getContents().size(); i++) {
            if (!getGhostItems().get(i).isEmpty()) {
                CompoundTag itemTag = new CompoundTag();
                itemTag.putInt("Slot", i);
                ghostItemNBT.add(getGhostItems().get(i).save(provider,itemTag));
            }
        }


        CompoundTag nbt = new CompoundTag();
        nbt.put("Items", nbtTagList);
        nbt.put(GHOST, ghostItemNBT);
        nbt.putBoolean("locked", frequencyLocked());
        nbt.putString("SortingType",sortingType.name());
        nbt.putBoolean("AutoSort",autoSort);
        return nbt;
    }

    public void load(HolderLookup.Provider provider,CompoundTag nbt) {
        ListTag tagList = nbt.getList("Items", Tag.TAG_COMPOUND);
        readItems(provider, tagList);
        ListTag ghostItemList = nbt.getList(GHOST, Tag.TAG_COMPOUND);
        readGhostItems(provider, ghostItemList);
        if (nbt.contains("locked")) {
            frequencyLocked = nbt.getBoolean("locked");
        }
        sortingType = nbt.contains("SortingType") ? SortingType.valueOf(nbt.getString("SortingType")) : SortingType.descending;
        if (nbt.contains("AutoSort")) {
            autoSort = nbt.getBoolean("AutoSort");
        }
    }

    public int calcRedstone() {
        int numStacks = 0;
        float f = 0F;

        for (int slot = 0; slot < this.slotCount(); slot++) {
            ItemStack stack = this.getItemDank(slot);

            if (!stack.isEmpty()) {
                f += (float) stack.getCount() / (float) this.getMaxStackSizeDank();
                numStacks++;
            }
        }

        f /= this.slotCount();
        return Mth.floor(f * 14F) + (numStacks > 0 ? 1 : 0);
    }

    public boolean noValidSlots() {
        return IntStream.range(0, slotCount())
                .mapToObj(this::getItemDank)
                .allMatch(stack -> stack.isEmpty() || stack.is(ModTags.BLACKLISTED_USAGE));
    }

    public void upgradeTo(DankStats stats) {
        if (stats.slots > slotCount()) {
            setTo(stats);
        }
    }

    //like upgradeTo, but can go backwards, should only be used by commands
    public void setTo(DankStats stats) {
            NonNullList<ItemStack> newStacks = NonNullList.withSize(stats.slots, ItemStack.EMPTY);
            NonNullList<ItemStack> newGhostStacks = NonNullList.withSize(stats.slots, ItemStack.EMPTY);

            //don't copy nonexistent items
            int oldSlots = slotCount();
            int max = Math.min(oldSlots, stats.slots);
            for (int i = 0; i < max; i++) {
                ItemStack oldStack = getItemDank(i);
                ItemStack oldGhost = getGhostItem(i);
                newStacks.set(i, oldStack);
                newGhostStacks.set(i, oldGhost);
            }

            //caution, will void all current items
            setItemsDank(newStacks);
            setGhostItems(newGhostStacks);
            setDirty();
    }


     public boolean hasGhostItem(int slot) {
        return !getGhostItems().get(slot).isEmpty();
    }

    public int getMaxStackSizeDank() {
        return capacity;
    }

    public void toggleGhostItem(int slot) {
        boolean loc = !getGhostItems().get(slot).isEmpty();
        if (!loc) {
            getGhostItems().set(slot, CommonUtils.copyStackWithSize(getItemDank(slot), 1));
        } else {
            getGhostItems().set(slot, ItemStack.EMPTY);
        }
        setDirty();
    }

    public int slotCount() {
        return items.size();
    }

    public void setDirty() {
        if (data != null) {
            data.setDirty();
        }
    }

    public List<ItemStack> getUniqueItems() {
        List<ItemStack> gathered = new ArrayList<>();
        for (ItemStack stack : items) {
            if (!stack.isEmpty()) {
                boolean matched = false;
                for (ItemStack itemStack : gathered) {
                    if (ItemStack.isSameItemSameComponents(itemStack, stack)) {
                        itemStack.grow(stack.getCount());
                        matched = true;
                        break;
                    }
                }
                if (!matched) {
                    gathered.add(stack.copy());
                }
            }
        }
        return gathered;
    }

    public long countItem(ItemStack sel) {
        if (sel.isEmpty()) return 0;
        long amount = items.stream().filter(stack -> ItemStack.isSameItemSameComponents(sel, stack)).mapToLong(ItemStack::getCount).sum();
        return amount;
    }
}
