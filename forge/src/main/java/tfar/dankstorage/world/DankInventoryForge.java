package tfar.dankstorage.world;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import tfar.dankstorage.DankStorageForge;
import tfar.dankstorage.ModTags;
import tfar.dankstorage.inventory.DankInterface;
import tfar.dankstorage.utils.CommonUtils;
import tfar.dankstorage.utils.DankStats;
import tfar.dankstorage.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class DankInventoryForge extends ItemStackHandler implements DankInterface {

    public DankStats dankStats;
    protected NonNullList<ItemStack> ghostItems;
    protected int frequency;
    private boolean frequencyLocked = true;
    protected int textColor = -1;

    public MinecraftServer server;

    public DankInventoryForge(DankStats stats, int frequency) {
        super(stats.slots);
        this.dankStats = stats;
        this.ghostItems = NonNullList.withSize(stats.slots,ItemStack.EMPTY);
        this.frequency = frequency;
    }

    public void setDankStats(DankStats dankStats) {
        this.dankStats = dankStats;
        setSize(dankStats.slots);
    }

    @Override
    public DankStats getDankStats() {
        return dankStats;
    }

    @Override
    public void setSize(int size) {
        super.setSize(size);
        ghostItems = NonNullList.withSize(size,ItemStack.EMPTY);
    }

    public void upgradeTo(DankStats stats) {
        //can't downgrade inventories
        if (stats.ordinal() <= dankStats.ordinal()) {
            return;
        }
        setTo(stats);
    }

    //like upgradeTo, but can go backwards, should only be used by commands
    public void setTo(DankStats stats) {
        if (stats != dankStats) {
            DankStorageForge.LOGGER.debug("Upgrading dank #{} from tier {} to {}", frequency, dankStats.name(), stats.name());
        }
        this.dankStats = stats;
        copyItems();
    }

    private void copyItems() {

        NonNullList<ItemStack> newStacks = NonNullList.withSize(dankStats.slots, ItemStack.EMPTY);
        NonNullList<ItemStack> newGhostStacks = NonNullList.withSize(dankStats.slots, ItemStack.EMPTY);

        //don't copy nonexistent items
        int oldSlots = getSlots();
        int max = Math.min(oldSlots, dankStats.slots);
        for (int i = 0; i < max;i++) {
            ItemStack oldStack = getStackInSlot(i);
            ItemStack oldGhost = getGhostItem(i);
            newStacks.set(i,oldStack);
            newGhostStacks.set(i,oldGhost);
        }

        //caution, will void all current items
        setSize(dankStats.slots);

        for (int i = 0; i < max; i++) {
            stacks = newStacks;
            ghostItems = newGhostStacks;
        }
        onContentsChanged(0);
    }

    @Override
    public int getContainerSizeDank() {
        return getSlots();
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        if (hasGhostItem(slot) && getGhostItem(slot).getItem() != stack.getItem()) {
            return stack;
        }
        return super.insertItem(slot, stack, simulate);
    }

    @Override
    public int getSlotLimit(int slot) {
        return dankStats.stacklimit;
    }

    @Override
    public int getStackLimit(int slot, @NotNull ItemStack stack) {
        return stack.is(ModTags.UNSTACKABLE) ? 1 : getSlotLimit(slot);
    }

    public NonNullList<ItemStack> getContents() {
        return stacks;
    }

    public boolean noValidSlots() {
        return IntStream.range(0, getSlots())
                .mapToObj(this::getStackInSlot)
                .allMatch(stack -> stack.isEmpty() || stack.is(ModTags.BLACKLISTED_USAGE));
    }

    public boolean hasGhostItem(int slot) {
        return !ghostItems.get(slot).isEmpty();
    }

    public ItemStack getGhostItem(int slot) {
        return ghostItems.get(slot);
    }

    public void setGhostItem(int slot,Item item) {
        ghostItems.set(slot,new ItemStack(item));
    }

    public void toggleGhostItem(int slot) {
        boolean loc = !ghostItems.get(slot).isEmpty();
        if (!loc) {
            ghostItems.set(slot,ItemHandlerHelper.copyStackWithSize(getStackInSlot(slot),1));
        } else {
            ghostItems.set(slot,ItemStack.EMPTY);
        }
        onContentsChanged(slot);
    }

    //paranoia
    @Override
    public boolean isItemValid(int slot,ItemStack stack) {
        boolean checkGhostItem = !hasGhostItem(slot) || getGhostItem(slot).getItem() == stack.getItem();
        return !stack.is(ModTags.BLACKLISTED_STORAGE)
                && checkGhostItem
                && super.isItemValid(slot, stack);
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


        ListTag ghostItemNBT = new ListTag();
        for (int i = 0; i < this.getContents().size(); i++) {
            if (!ghostItems.get(i).isEmpty()) {
                CompoundTag itemTag = new CompoundTag();
                itemTag.putInt("Slot", i);
                ghostItems.get(i).save(itemTag);
                ghostItemNBT.add(itemTag);
            }
        }


        CompoundTag nbt = new CompoundTag();
        nbt.put("Items", nbtTagList);
        nbt.put(GHOST,ghostItemNBT);
        nbt.putString("DankStats", dankStats.name());
        nbt.putInt(Utils.FREQ, frequency);
        nbt.putBoolean("locked", frequencyLocked);
        return nbt;
    }

    public void read(CompoundTag nbt) {
        DankStats stats = DankStats.valueOf(nbt.getString("DankStats"));
        setDankStats(stats);
        ListTag tagList = nbt.getList("Items", Tag.TAG_COMPOUND);
        readItems(tagList);
        ListTag ghostItemList = nbt.getList(GHOST,Tag.TAG_COMPOUND);
        readGhostItems(ghostItemList);
        frequencyLocked = nbt.getBoolean("locked");
        validate();
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
        if (server != null) {
            DankStorageForge.getData(frequency,server).write(save());
        }
    }

    public int getFrequencySlot() {
        return getSlots();
    }

    public int getTextColor() {
        return get(1);
    }

    public void setTextColor(int color) {
        set(1, color);
    }

    public boolean frequencyLocked() {
        return get(2) == 1;
    }

    public void toggleFrequencyLock() {
        boolean loc = frequencyLocked();
        setFrequencyLock(!loc);
    }

    public void setFrequencyLock(boolean lock) {
        set(2, lock ? 1 : 0);
    }

    @Override
    public int get(int slot) {
        return switch (slot) {
            case 0 -> frequency;
            case 1 -> textColor;
            case 2 -> frequencyLocked ? 1 : 0;
            default -> AbstractContainerMenu.SLOT_CLICKED_OUTSIDE;
        };
    }

    @Override
    public void set(int slot, int value) {
        switch (slot) {
            case 0 -> frequency = value;
            case 1 -> textColor = value;
            case 2 -> frequencyLocked = value == 1;
        }
        onContentsChanged(slot);
    }

    @Override
    public int getMaxStackSizeDank() {
        return getSlotLimit(0);
    }

    public void compress(ServerPlayer player) {
        sort();
        ServerLevel level = player.serverLevel();
        List<ItemStack> addLater = new ArrayList<>();
        for (int i = 0; i < getSlots() ; i++) {
            ItemStack stack = getStackInSlot(i);
            if (stack.isEmpty()) {
                break;
            }
            if (CommonUtils.canCompress(level,stack)) {
                Pair<ItemStack,Integer> result = CommonUtils.compress(stack,player.serverLevel().registryAccess());
                ItemStack resultStack = result.getFirst();
                if (!resultStack.isEmpty()) {
                    int division = result.getSecond();
                    int compressedCount = stack.getCount() / division;
                    int remainderCount = stack.getCount() % division;
                    setStackInSlot(i,ItemHandlerHelper.copyStackWithSize(resultStack,compressedCount));
                    addLater.add(ItemHandlerHelper.copyStackWithSize(stack,remainderCount));
                }
            }
        }
        sort();

        for (ItemStack itemStack : addLater) {
            ItemStack remainder = itemStack.copy();
            for (int i = 0; i < getSlots();i++) {
                remainder = insertItem(i,remainder,false);
                if (remainder.isEmpty()) break;
            }
            if (!remainder.isEmpty()) {
                ItemHandlerHelper.giveItemToPlayer(player,remainder);
            }
        }
        sort();
    }

    @Override
    public void setItemDank(int slot, ItemStack stack) {
        setStackInSlot(slot,stack);
    }

    @Override
    public ItemStack getItemDank(int slot) {
        return getStackInSlot(slot);
    }

    @Override
    public NonNullList<ItemStack> getGhostItems() {
        return ghostItems;
    }

    //0 - 80 are locked slots, 81 is the id, 82 is text color, and 83 is global lock

    @Override
    public int getCount() {
        return getSlots() + 3;
    }
}
