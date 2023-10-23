package tfar.dankstorage.world;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
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
import tfar.dankstorage.utils.DankStats;
import tfar.dankstorage.utils.Utils;

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

    public ItemStack getGhostItem(int slot) {
        return ghostItems.get(slot);
    }

    public void setGhostItem(int slot,Item item) {
        ghostItems.set(slot,new ItemStack(item));
    }

    //paranoia
    @Override
    public boolean isItemValid(int slot,ItemStack stack) {
        boolean checkGhostItem = !hasGhostItem(slot) || getGhostItem(slot).getItem() == stack.getItem();
        return !stack.is(ModTags.BLACKLISTED_STORAGE)
                && checkGhostItem
                && super.isItemValid(slot, stack);
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
    public void setChanged() {
        onContentsChanged(0);
    }

    @Override
    public MinecraftServer getServer() {
        return server;
    }

    @Override
    public void setServer(MinecraftServer server) {
        this.server = server;
    }

    @Override
    public void onContentsChanged(int slot) {
        super.onContentsChanged(slot);
        if (server != null) {
            DankStorageForge.getData(frequency,server).write(save());
        }
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

    @Override
    public void setItemDank(int slot, ItemStack stack) {
        setStackInSlot(slot,stack);
    }

    @Override
    public ItemStack addItemDank(int slot, ItemStack stack) {
        return insertItem(slot,stack,false);
    }

    @Override
    public ItemStack getItemDank(int slot) {
        return getStackInSlot(slot);
    }

    @Override
    public NonNullList<ItemStack> getGhostItems() {
        return ghostItems;
    }

}
