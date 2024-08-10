package tfar.dankstorage.inventory;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import tfar.dankstorage.DankStorage;
import tfar.dankstorage.ModTags;
import tfar.dankstorage.platform.Services;
import tfar.dankstorage.utils.CommonUtils;
import tfar.dankstorage.utils.DankStats;
import tfar.dankstorage.utils.ItemStackWrapper;

import java.util.*;
import java.util.stream.IntStream;

public interface DankInterface extends ContainerData {

    String GHOST = "GhostItems";

    int FREQ = 0;
    int TXT_COLOR = 1;
    int FREQ_LOCK = 2;

    static DankInterface createDummy(DankStats stats) {
        return Services.PLATFORM.createInventory(stats, CommonUtils.INVALID);
    }

    default ItemStack getGhostItem(int slot) {
        return getGhostItems().get(slot);
    }


    DankStats getDankStats();

    default boolean frequencyLocked() {
        return get(FREQ_LOCK) == 1;
    }

    default int textColor() {
        return get(TXT_COLOR);
    }

    default void setTextColor(int color) {
        set(TXT_COLOR, color);
    }

    default void toggleFrequencyLock() {
        boolean loc = frequencyLocked();
        setFrequencyLock(!loc);
    }

    default void setFrequencyLock(boolean lock) {
        set(FREQ_LOCK, lock ? 1 : 0);
    }

    void setItemDank(int slot, ItemStack stack);

    ItemStack getItemDank(int slot);

    default void setGhostItem(int slot, Item item) {
        getGhostItems().set(slot, new ItemStack(item));
    }

    NonNullList<ItemStack> getContents();
    void setItemsDank(NonNullList<ItemStack> stacks);

    NonNullList<ItemStack> getGhostItems();
    void setGhostItems(NonNullList<ItemStack> stacks);

    default int frequency() {
        return get(FREQ);
    }


    //0 is the id, 1 is text color, and 2 is frequency lock
    @Override
    default int getCount() {
        return 3;
    }

    int getContainerSizeDank();

    ItemStack addItemDank(int slot, ItemStack stack);

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

    default int getMaxStackSizeSensitive(ItemStack stack) {
        return stack.is(ModTags.UNSTACKABLE) ? 1 : getMaxStackSizeDank();
    }

    default void sort() {
        List<ItemStack> stacks = new ArrayList<>();

        Set<ItemStack> lockedItems = new HashSet<>();


        NonNullList<ItemStack> contents = getContents();
        for (int i = 0; i < contents.size(); i++) {
            ItemStack stack = contents.get(i);
            ItemStack ghost = getGhostItem(i);
            if (!stack.isEmpty()) {
                CommonUtils.merge(stacks, stack.copy());
                boolean unique = true;
                for (ItemStack stack1 : lockedItems) {
                    if (ItemStack.isSameItemSameTags(stack1, stack)) {
                        unique = false;
                    }
                }
                if (unique && !ghost.isEmpty()) {
                    lockedItems.add(stack.copyWithCount(1));
                }
            }
        }

        List<ItemStackWrapper> wrappers = CommonUtils.wrap(stacks);

        Collections.sort(wrappers);



        for (int i = 0; i < getContainerSizeDank(); i++) {
            setItemDank(i, ItemStack.EMPTY);
            getGhostItems().set(i, ItemStack.EMPTY);
        }



        //split up the stacks and add them to the slot

        int slotId = 0;

        for (int i = 0; i < wrappers.size(); i++) {
            ItemStack stack = wrappers.get(i).stack();
            int count = stack.getCount();

            int stackSizeSensitive = getMaxStackSizeSensitive(stack);

            if (count > stackSizeSensitive) {
                int fullStacks = count / stackSizeSensitive;
                int partialStack = count - fullStacks * stackSizeSensitive;

                for (int j = 0; j < fullStacks; j++) {
                    setItemDank(slotId, CommonUtils.copyStackWithSize(stack, stackSizeSensitive));

                    if (anyMatch(stack,lockedItems)) {
                        setGhostItem(slotId,stack.getItem());
                    }

                    slotId++;
                }
                if (partialStack > 0) {
                    setItemDank(slotId, CommonUtils.copyStackWithSize(stack, partialStack));

                    if (anyMatch(stack,lockedItems)) {
                        setGhostItem(slotId,stack.getItem());
                    }

                    slotId++;
                }
            } else {
                setItemDank(slotId, stack);


                if (anyMatch(stack,lockedItems)) {
                    setGhostItem(slotId,stack.getItem());
                }

                slotId++;
            }
        }
    }

    static boolean anyMatch(ItemStack stack, Set<ItemStack> stacks) {
        for (ItemStack stack1 : stacks) {
            if (ItemStack.isSameItemSameTags(stack1,stack)) {
                return true;
            }
        }
        return false;
    }

    default void compress(ServerPlayer player) {
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
            for (int i = 0; i < getContainerSizeDank(); i++) {
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

    MinecraftServer getServer();

    void setServer(MinecraftServer server);

    void setDankStats(DankStats dankStats);


    default CompoundTag save() {
        ListTag nbtTagList = new ListTag();
        for (int i = 0; i < this.getContents().size(); i++) {
            if (!getContents().get(i).isEmpty()) {
                int realCount = Math.min(getDankStats().stacklimit, getContents().get(i).getCount());
                CompoundTag itemTag = new CompoundTag();
                itemTag.putInt("Slot", i);
                getContents().get(i).save(itemTag);
                itemTag.putInt("ExtendedCount", realCount);
                nbtTagList.add(itemTag);
            }
        }


        ListTag ghostItemNBT = new ListTag();
        for (int i = 0; i < this.getContents().size(); i++) {
            if (!getGhostItems().get(i).isEmpty()) {
                CompoundTag itemTag = new CompoundTag();
                itemTag.putInt("Slot", i);
                getGhostItems().get(i).save(itemTag);
                ghostItemNBT.add(itemTag);
            }
        }


        CompoundTag nbt = new CompoundTag();
        nbt.put("Items", nbtTagList);
        nbt.put(GHOST, ghostItemNBT);
        nbt.putString("DankStats", getDankStats().name());
        nbt.putBoolean("locked", frequencyLocked());
        return nbt;
    }

    default void read(CompoundTag nbt) {
        DankStats stats = DankStats.valueOf(nbt.getString("DankStats"));
        setDankStats(stats);
        ListTag tagList = nbt.getList("Items", Tag.TAG_COMPOUND);
        readItems(tagList);
        ListTag ghostItemList = nbt.getList(GHOST, Tag.TAG_COMPOUND);
        readGhostItems(ghostItemList);
        setFrequencyLock(nbt.getBoolean("locked"));
        validate();
    }

    default int calcRedstone() {
        int numStacks = 0;
        float f = 0F;

        for (int slot = 0; slot < this.getContainerSizeDank(); slot++) {
            ItemStack stack = this.getItemDank(slot);

            if (!stack.isEmpty()) {
                f += (float) stack.getCount() / (float) this.getMaxStackSizeDank();
                numStacks++;
            }
        }

        f /= this.getContainerSizeDank();
        return Mth.floor(f * 14F) + (numStacks > 0 ? 1 : 0);
    }

    default boolean noValidSlots() {
        return IntStream.range(0, getContainerSizeDank())
                .mapToObj(this::getItemDank)
                .allMatch(stack -> stack.isEmpty() || stack.is(ModTags.BLACKLISTED_USAGE));
    }

    default void upgradeTo(DankStats stats) {
        //can't downgrade inventories
        if (stats.ordinal() <= getDankStats().ordinal()) {
            return;
        }
        setTo(stats);
    }

    //like upgradeTo, but can go backwards, should only be used by commands
    default void setTo(DankStats stats) {
        if (stats != getDankStats()) {
            DankStorage.LOG.debug("Upgrading dank #{} from tier {} to {}", frequency(), getDankStats().name(), stats.name());


            NonNullList<ItemStack> newStacks = NonNullList.withSize(stats.slots, ItemStack.EMPTY);
            NonNullList<ItemStack> newGhostStacks = NonNullList.withSize(stats.slots, ItemStack.EMPTY);

            //don't copy nonexistent items
            int oldSlots = getContainerSizeDank();
            int max = Math.min(oldSlots, stats.slots);
            for (int i = 0; i < max; i++) {
                ItemStack oldStack = getItemDank(i);
                ItemStack oldGhost = getGhostItem(i);
                newStacks.set(i, oldStack);
                newGhostStacks.set(i, oldGhost);
            }

            //caution, will void all current items
            setDankStats(stats);
            setItemsDank(newStacks);
            setGhostItems(newGhostStacks);
            setChangedDank();
        }
    }


    default boolean hasGhostItem(int slot) {
        return !getGhostItems().get(slot).isEmpty();
    }

    int getMaxStackSizeDank();

    default void validate() {
        int containerSizeDank = getContainerSizeDank();
        if (getDankStats() == DankStats.zero) {
            DankStorage.LOG.error("dank has no stats?");
        } else if (containerSizeDank == 0) {
            DankStorage.LOG.error("dank is empty?");
        } else {
            if (getGhostItems().size() != containerSizeDank) {
                DankStorage.LOG.error("inequal size");
            }
        }
    }

    default void copyItems() {


    }

    default void toggleGhostItem(int slot) {
        boolean loc = !getGhostItems().get(slot).isEmpty();
        if (!loc) {
            getGhostItems().set(slot, CommonUtils.copyStackWithSize(getItemDank(slot), 1));
        } else {
            getGhostItems().set(slot, ItemStack.EMPTY);
        }
        setChangedDank();
    }

    void setChangedDank();

    void setSizeDank(int size);

    default void saveToDisk() {
        if (getServer() != null) {
            DankStorage.getData(frequency(), getServer()).write(save());
        }
    }

    default boolean valid() {
        return getDankStats() != DankStats.zero;
    }

}
