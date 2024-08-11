package tfar.dankstorage.world;

import net.minecraft.core.NonNullList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import tfar.dankstorage.ModTags;
import tfar.dankstorage.inventory.DankInterface;
import tfar.dankstorage.mixin.SimpleContainerAccess;
import tfar.dankstorage.utils.*;

public class DankInventoryFabric extends SimpleContainer implements DankInterface {

    public DankStats dankStats;
    protected NonNullList<ItemStack> ghostItems;
    protected int frequency;
    public boolean frequencyLocked = true;

    protected int textColor = -1;

    public MinecraftServer server;

    public DankInventoryFabric(DankStats stats, int frequency) {
        super(stats.slots);
        this.dankStats = stats;
        this.ghostItems = NonNullList.withSize(stats.slots, ItemStack.EMPTY);
        this.frequency = frequency;
    }

    @Override
    public DankStats getDankStats() {
        return dankStats;
    }
    @Override
    public void setSizeDank(int size) {
        $setSize(size);
    }

    @Override
    public int getContainerSizeDank() {
        return getContainerSize();
    }

    public void setGhostItems(NonNullList<ItemStack> newGhosts) {
        ghostItems = newGhosts;
    }

    @Override
    public void setItemsDank(NonNullList<ItemStack> stacks) {
        ((SimpleContainerAccess)this).setItems(stacks);
    }

    @Override
    public ItemStack addItemDank(int slot, ItemStack stack) {
        return addStack(slot,stack);
    }

    ItemStack addStack(int slot,ItemStack stack) {
        ItemStack existing = this.getItem(slot);
        if (ItemStack.isSameItemSameComponents(existing, stack)) {
            this.moveItemsBetweenStacks(stack, existing);
            if (stack.isEmpty()) {
                return ItemStack.EMPTY;
            }
        }
        return stack;
    }

    private void moveItemsBetweenStacks(ItemStack itemStack, ItemStack itemStack2) {
        int i = Math.min(this.getMaxStackSize(), itemStack2.getMaxStackSize());
        int j = Math.min(itemStack.getCount(), i - itemStack2.getCount());
        if (j > 0) {
            itemStack2.grow(j);
            itemStack.shrink(j);
            this.setChangedDank();
        }
    }

    @Override
    public NonNullList<ItemStack> getGhostItems() {
        return ghostItems;
    }

    //distinguish from the mixin accessor
    public void $setSize(int size) {
        ((SimpleContainerAccess)this).setSize(size);
        ((SimpleContainerAccess) this).setItems(NonNullList.withSize(size, ItemStack.EMPTY));
        setGhostItems(NonNullList.withSize(size, ItemStack.EMPTY));
    }

    public void setDankStats(DankStats dankStats) {
        this.dankStats = dankStats;
        $setSize(dankStats.slots);
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
    public int getMaxStackSize() {
        return dankStats.stacklimit;
    }

    public NonNullList<ItemStack> getContents() {
        return items;
    }


    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        boolean checkGhostItem = !hasGhostItem(slot) || getGhostItem(slot).getItem() == stack.getItem();
        return !stack.is(ModTags.BLACKLISTED_STORAGE)
                && checkGhostItem;
    }

    //paranoia
    /*
    @Override


    public boolean canAddItem(ItemStack stack) {
        return !stack.is(Utils.BLACKLISTED_STORAGE) && super.canAddItem(stack);
    }

    //returns the portion of the itemstack that was NOT placed into the storage
    @Override
    public ItemStack addItem(ItemStack itemStack) {
        return itemStack.is(Utils.BLACKLISTED_STORAGE) ? itemStack : super.addItem(itemStack);
    }*/

    @Override
    public int getMaxStackSizeDank() {
        return getMaxStackSize();
    }

    @Override
    public void setChangedDank() {
        setChanged();
    }

    @Override
    public void setChanged() {
        super.setChanged();
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
        setChangedDank();
    }

    @Override
    public void setItemDank(int slot, ItemStack stack) {
        setItem(slot, stack);
    }

    @Override
    public ItemStack getItemDank(int slot) {
        return getItem(slot);
    }

}
