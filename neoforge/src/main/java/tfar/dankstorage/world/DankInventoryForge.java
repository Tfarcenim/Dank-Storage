package tfar.dankstorage.world;

import net.minecraft.core.NonNullList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import tfar.dankstorage.ModTags;
import tfar.dankstorage.inventory.DankInterface;
import tfar.dankstorage.utils.DankStats;

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


    @Override
    public void setSizeDank(int size) {
        setSize(size);
    }

    @Override
    public void setItemsDank(NonNullList<ItemStack> stacks) {
        this.stacks = stacks;
    }

    @Override
    public void setGhostItems(NonNullList<ItemStack> ghostItems) {
        this.ghostItems = ghostItems;
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
        return getMaxStackSizeSensitive(stack);
    }

    public NonNullList<ItemStack> getContents() {
        return stacks;
    }

    //paranoia
    @Override
    public boolean isItemValid(int slot,ItemStack stack) {
        boolean checkGhostItem = !hasGhostItem(slot) || getGhostItem(slot).getItem() == stack.getItem();
        return !stack.is(ModTags.BLACKLISTED_STORAGE)
                && checkGhostItem
                && super.isItemValid(slot, stack);
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
    public void setChangedDank() {
       onContentsChanged(0);
    }

    @Override
    protected void onContentsChanged(int slot) {
        super.onContentsChanged(slot);
        saveToDisk();
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
