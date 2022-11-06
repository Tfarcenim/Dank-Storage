package tfar.dankstorage.world;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import tfar.dankstorage.DankStorage;
import tfar.dankstorage.utils.DankStats;
import tfar.dankstorage.utils.ItemHandlerHelper;
import tfar.dankstorage.utils.ItemStackWrapper;
import tfar.dankstorage.utils.Utils;

import java.util.*;
import java.util.stream.IntStream;

public class DankInventory extends ItemStackHandler implements ContainerData {

    public DankStats dankStats;
    protected int[] lockedSlots;
    protected int id;
    public boolean locked = true;

    protected int textColor = -1;

    public DankInventory(DankStats stats, int id) {
        super(stats.slots);
        this.dankStats = stats;
        this.lockedSlots = new int[stats.slots];
        this.id = id;
    }

    public void upgradeTo(DankStats stats) {

        //can't downgrade inventories
        if (stats.ordinal() <= dankStats.ordinal()) {
            return;
        }
        DankStorage.LOGGER.debug("Upgrading dank #{} from tier {} to {}", id, dankStats.name(), stats.name());
        setTo(stats);
    }

    //like upgradeTo, but can go backwards, should only be used by commands
    public void setTo(DankStats stats) {
        this.dankStats = stats;
        fixLockedSlots();
    }

    private void fixLockedSlots() {
        setSize(dankStats.slots);

        NonNullList<ItemStack> newStacks = NonNullList.withSize(dankStats.slots, ItemStack.EMPTY);
        int max = Math.min(lockedSlots.length, dankStats.slots);

        for (int i = 0; i < max; i++) {
            newStacks.set(i, getContents().get(i));
        }

         stacks = newStacks;

        int[] newLockedSlots = new int[dankStats.slots];
        if (max >= 0) System.arraycopy(lockedSlots, 0, newLockedSlots, 0, max);
        lockedSlots = newLockedSlots;
        onContentsChanged(0);
    }

    @Override
    public ItemStack extractItem(int slot, int amount,boolean simulate) {
        if (!isLocked(slot)) {
            return super.extractItem(slot, amount,simulate);
        }

        int amountInSlot = getStackInSlot(slot).getCount();

        if (amountInSlot < amount) {
            return super.extractItem(slot, amount,simulate);
        }

        amount = Math.min(amount, amountInSlot - 1);

        if (amount == 0) {
            return ItemStack.EMPTY;
        }

        ItemStack itemStack = ContainerHelper.removeItem(getContents(), slot, amount);
        if (!itemStack.isEmpty()) {
            this.onContentsChanged(slot);
        }

        return itemStack;
    }

    @Override
    public int getSlotLimit(int slot) {
        return dankStats.stacklimit;
    }

    public NonNullList<ItemStack> getContents() {
        return stacks;
    }

    public boolean noValidSlots() {
        return IntStream.range(0, getSlots())
                .mapToObj(this::getStackInSlot)
                .allMatch(stack -> stack.isEmpty() || stack.is(Utils.BLACKLISTED_USAGE));
    }

    public boolean isLocked(int slot) {
        return get(slot) == 1;
    }

    public void toggleSlotLock(int slot) {
        boolean loc = get(slot) == 1;
        set(slot, loc ? 0 : 1);
        onContentsChanged(slot);
    }

    //paranoia
    @Override
    public boolean isItemValid(int slot,ItemStack stack) {
        return !stack.is(Utils.BLACKLISTED_STORAGE) && super.isItemValid(slot, stack);
    }

    //returns the portion of the itemstack that was NOT placed into the storage
    public CompoundTag save() {
        ListTag nbtTagList = new ListTag();
        for (int i = 0; i < this.getContents().size(); i++) {
            if (!getContents().get(i).isEmpty()) {
                int realCount = Math.min(dankStats.stacklimit, getContents().get(i).getCount());
                CompoundTag itemTag = new CompoundTag();
                itemTag.putInt("Slot", i);
                getContents().get(i).save(itemTag);
                itemTag.putInt("ExtendedCount", realCount);
                nbtTagList.add(itemTag);
            }
        }

        CompoundTag nbt = new CompoundTag();
        nbt.put("Items", nbtTagList);
        nbt.putIntArray("LockedSlots", lockedSlots);
        nbt.putString("DankStats", dankStats.name());
        nbt.putInt(Utils.ID, id);
        nbt.putBoolean("locked", locked);
        return nbt;
    }

    public void read(CompoundTag nbt) {
        DankStats stats = DankStats.valueOf(nbt.getString("DankStats"));
        upgradeTo(stats);
        ListTag tagList = nbt.getList("Items", Tag.TAG_COMPOUND);
        locked = nbt.getBoolean("locked");
        for (int i = 0; i < tagList.size(); i++) {
            CompoundTag itemTags = tagList.getCompound(i);
            int slot = itemTags.getInt("Slot");
            if (slot >= 0 && slot < getSlots()) {
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
                        count = Math.min(count, getSlotLimit(slot));
                        stack.setCount(count);

                        this.setStackInSlot(slot, stack);
                    }
                } else {
                    ItemStack stack = ItemStack.of(itemTags);
                    if (itemTags.contains("ExtendedCount", Tag.TAG_INT)) {
                        stack.setCount(itemTags.getInt("ExtendedCount"));
                    }
                    this.setStackInSlot(slot, stack);
                }
            }
        }
        int[] slots = nbt.getIntArray("LockedSlots");
        setLockedSlots(slots);
        validate();
    }

    protected void setLockedSlots(int[] slots) {
        System.arraycopy(slots, 0, this.lockedSlots, 0, slots.length);
    }

    protected void validate() {
        if (dankStats == DankStats.zero) {
            throw new RuntimeException("dank has no stats?");
        } else if (getSlots() == 0) {
            throw new RuntimeException("dank is empty?");
        } else {
            if (lockedSlots.length != getSlots()) {
                throw new RuntimeException("inequal size");
            }
        }
    }

    public int calcRedstone() {
        int numStacks = 0;
        float f = 0F;

        for (int slot = 0; slot < this.getSlots(); slot++) {
            ItemStack stack = this.getStackInSlot(slot);

            if (!stack.isEmpty()) {
                f += (float) stack.getCount() / (float) this.getSlotLimit(slot);
                numStacks++;
            }
        }

        f /= this.getSlots();
        return Mth.floor(f * 14F) + (numStacks > 0 ? 1 : 0);
    }
    @Override
    public void onContentsChanged(int slot) {
        super.onContentsChanged(slot);
        if (DankStorage.instance.data != null) {
            DankStorage.instance.data.setDirty();
        }
    }

    public int getFrequencySlot() {
        return getSlots();
    }

    public int getTextColor() {
        return get(getFrequencySlot() + 1);
    }

    public void setTextColor(int color) {
        set(getFrequencySlot() + 1, color);
    }

    public boolean frequencyLocked() {
        return get(getFrequencySlot() + 2) == 1;
    }

    public void toggleFrequencyLock() {
        boolean loc = frequencyLocked();
        setFrequencyLock(!loc);
    }

    public void setFrequencyLock(boolean lock) {
        set(getFrequencySlot() + 2, lock ? 1 : 0);
    }

    @Override
    public int get(int slot) {
        if (slot < getSlots()) {
            return lockedSlots[slot];
        } else if (slot == getFrequencySlot()) {
            return id;
        } else if (slot == getFrequencySlot() + 1) {
            return textColor;
        } else if (slot == getFrequencySlot() + 2) {
            return locked ? 1 : 0;
        }
        return -999;
    }

    public int getFrequency() {
        return get(getFrequencySlot());
    }

    @Override
    public void set(int slot, int value) {
        if (slot < getSlots()) {
            lockedSlots[slot] = value;
        } else if (slot == getFrequencySlot()) {
            id = value;
        } else if (slot == getFrequencySlot() + 1) {
            textColor = value;
        } else if (slot == getFrequencySlot() + 2) {
            locked = value == 1;
        }
        onContentsChanged(slot);
    }

    public void compress(ServerLevel level) {
        sort();
        int freeSlots = 0;

        Map<Item, Pair<Integer, Integer>> groups = new HashMap<>();

        for (int i = 0; i < getSlots(); i++) {
            ItemStack stack = getStackInSlot(i);
            if (!stack.isEmpty()) {

                if (Utils.canCompress(level, stack)) {

                    Item item = stack.getItem();
                    Pair<Integer, Integer> pair;
                    if (groups.containsKey(item)) {
                        pair = Pair.of(groups.get(item).getFirst(), i);
                    } else {
                        pair = Pair.of(i, i);
                    }
                    groups.put(item, pair);
                }
            } else {
                freeSlots = getSlots() - i;
                break;
            }
        }

        List<Item> unsafeSlots = new ArrayList<>();
        for (Map.Entry<Item, Pair<Integer, Integer>> entry : groups.entrySet()) {
            Item item = entry.getKey();
            Pair<Integer, Integer> pair = entry.getValue();
            Pair<ItemStack, Integer> stackIntegerPair = Utils.compress(new ItemStack(item));
            int count = 0;

            for (int i = pair.getFirst(); i < pair.getSecond() + 1; i++) {
                count += getStackInSlot(i).getCount();
            }
            if (count <= getSlots() && count % stackIntegerPair.getSecond() != 0) {
                unsafeSlots.add(item);
            } else {
                ItemStack compressionResult = stackIntegerPair.getFirst();
                int compressedCount = count / stackIntegerPair.getSecond();
                int remainder = count % stackIntegerPair.getSecond();


                //clear out old items
                for (int i = pair.getFirst(); i < pair.getSecond() + 1; i++) {
                    extractItem(i, getSlots(),false);
                }
                int fullStacks = compressedCount / getSlots();

                int partialStack = compressedCount % getSlots();

                //set max stacksize items
                for (int i = pair.getFirst(); i < pair.getFirst() + fullStacks; i++) {
                    setStackInSlot(i, new ItemStack(compressionResult.getItem(), getSlots()));
                }

                //set partial stack of compressed items
                if (partialStack > 0) {
                    setStackInSlot(pair.getFirst() + fullStacks, new ItemStack(compressionResult.getItem(), partialStack));
                }

                if (remainder > 0) {
                    setStackInSlot(pair.getFirst() + fullStacks + 1, new ItemStack(item, partialStack));
                }
            }
        }
        sort();


        for (Item item : unsafeSlots) {
            if (freeSlots <= 0) {
                break;
            } else {
                for (int i = 0; i < getSlots(); i++) {
                    ItemStack stack = getStackInSlot(i);
                    if (stack.getItem() == item) {

                        Pair<ItemStack, Integer> stackIntegerPair = Utils.compress(new ItemStack(item));

                        ItemStack compressionResult = stackIntegerPair.getFirst();
                        int compressedCount = stack.getCount() / stackIntegerPair.getSecond();
                        int remainder = stack.getCount() % stackIntegerPair.getSecond();

                        setStackInSlot(i, new ItemStack(compressionResult.getItem(), compressedCount));
                        setStackInSlot(getSlots() - freeSlots, new ItemStack(item, remainder));
                        freeSlots--;
                        break;
                    }
                }
            }
        }
        sort();
    }

    public void sort() {
        List<ItemStack> stacks = new ArrayList<>();

        for (ItemStack stack : getContents()) {
            if (!stack.isEmpty()) {
                Utils.merge(stacks, stack.copy());
            }
        }

        List<ItemStackWrapper> wrappers = Utils.wrap(stacks);

        Collections.sort(wrappers);

        this.stacks.clear();

        //split up the stacks and add them to the slot

        int slotId = 0;

        for (int i = 0; i < wrappers.size(); i++) {
            ItemStack stack = wrappers.get(i).stack;
            int count = stack.getCount();
            if (count > dankStats.stacklimit) {
                int fullStacks = count / dankStats.stacklimit;
                int partialStack = count - fullStacks * dankStats.stacklimit;

                for (int j = 0; j < fullStacks;j++) {
                    setStackInSlot(slotId, ItemHandlerHelper.copyStackWithSize(stack, dankStats.stacklimit));
                    slotId++;
                }
                if (partialStack > 0) {
                    setStackInSlot(slotId,  ItemHandlerHelper.copyStackWithSize(stack, partialStack));
                    slotId++;
                }
            } else {
                setStackInSlot(slotId,stack);
                slotId++;
            }
            //setStackInSlot(i, stack);
        }
    }

    public void clearContent() {
        stacks.clear();
        onContentsChanged(0);
    }

    public enum TxtColor {
        INVALID(0xffff0000), TOO_HIGH(0xffff8000), DIFFERENT_TIER(0xffffff00), GOOD(0xff00ff00), LOCKED(0xff0000ff);
        public final int color;

        TxtColor(int color) {
            this.color = color;
        }
    }

    //0 - 80 are locked slots, 81 is the id, 82 is text color, and 83 is global lock

    @Override
    public int getCount() {
        return getSlots() + 3;
    }
}
