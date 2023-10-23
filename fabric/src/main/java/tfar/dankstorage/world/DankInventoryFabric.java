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
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import tfar.dankstorage.Constants;
import tfar.dankstorage.DankStorageFabric;
import tfar.dankstorage.ModTags;
import tfar.dankstorage.inventory.DankInterface;
import tfar.dankstorage.mixin.SimpleContainerAccess;
import tfar.dankstorage.utils.*;

import java.util.*;
import java.util.stream.IntStream;

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

    public void upgradeTo(DankStats stats) {

        //can't downgrade inventories
        if (stats.ordinal() <= dankStats.ordinal()) {
            return;
        }
        Constants.LOG.debug("Upgrading dank #{} from tier {} to {}", frequency, dankStats.name(), stats.name());
        setTo(stats);
    }

    //like upgradeTo, but can go backwards, should only be used by commands
    public void setTo(DankStats stats) {
        this.dankStats = stats;
        copyItems();
    }

    @Override
    public int getContainerSizeDank() {
        return getContainerSize();
    }

    private void copyItems() {

        NonNullList<ItemStack> newStacks = NonNullList.withSize(dankStats.slots, ItemStack.EMPTY);
        NonNullList<ItemStack> newGhostStacks = NonNullList.withSize(dankStats.slots, ItemStack.EMPTY);

        //don't copy nonexistent items
        int oldSlots = getContainerSize();
        int max = Math.min(oldSlots, dankStats.slots);
        for (int i = 0; i < max; i++) {
            ItemStack oldStack = getItem(i);
            ItemStack oldGhost = getGhostItem(i);
            newStacks.set(i, oldStack);
            newGhostStacks.set(i, oldGhost);
        }

        //caution, will void all current items
        $setSize(dankStats.slots);

        ((SimpleContainerAccess) this).setItems(newStacks);
        setGhostItems(newGhostStacks);
        setChanged();
    }

    protected void setGhostItems(NonNullList<ItemStack> newGhosts) {
        ghostItems = newGhosts;
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
    public int getMaxStackSize() {
        return dankStats.stacklimit;
    }

    public NonNullList<ItemStack> getContents() {
        return items;
    }

    public boolean noValidSlots() {
        return IntStream.range(0, getContainerSize())
                .mapToObj(this::getItem)
                .allMatch(stack -> stack.isEmpty() || stack.is(ModTags.BLACKLISTED_USAGE));
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



    public CompoundTag save() {
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
            if (!ghostItems.get(i).isEmpty()) {
                CompoundTag itemTag = new CompoundTag();
                itemTag.putInt("Slot", i);
                ghostItems.get(i).save(itemTag);
                ghostItemNBT.add(itemTag);
            }
        }


        CompoundTag nbt = new CompoundTag();
        nbt.put("Items", nbtTagList);
        nbt.put(GHOST, ghostItemNBT);
        nbt.putString("DankStats", dankStats.name());
        nbt.putInt(Utils.FREQ, getFrequency());
        nbt.putBoolean("locked", frequencyLocked());
        return nbt;
    }

    public void read(CompoundTag nbt) {
        DankStats stats = DankStats.valueOf(nbt.getString("DankStats"));
        setDankStats(stats);
        ListTag tagList = nbt.getList("Items", Tag.TAG_COMPOUND);
        readItems(tagList);
        ListTag ghostItemList = nbt.getList(GHOST, Tag.TAG_COMPOUND);
        readGhostItems(ghostItemList);
        frequencyLocked = nbt.getBoolean("locked");
        validate();
    }

    @Override
    public int getMaxStackSizeDank() {
        return getMaxStackSize();
    }

    public int calcRedstone() {
        int numStacks = 0;
        float f = 0F;

        for (int slot = 0; slot < this.getContainerSize(); slot++) {
            ItemStack stack = this.getItem(slot);

            if (!stack.isEmpty()) {
                f += (float) stack.getCount() / (float) this.getMaxStackSize();
                numStacks++;
            }
        }

        f /= this.getContainerSize();
        return Mth.floor(f * 14F) + (numStacks > 0 ? 1 : 0);
    }


    @Override
    public void setChanged() {
        super.setChanged();
        if (server != null) {
            DankStorageFabric.getData(frequency,server).write(save());
        }
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
        boolean loc = get(2) == 1;
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
        setChanged();
    }

    public void compress(ServerPlayer player) {
        sort();
        ServerLevel level = player.serverLevel();
        List<ItemStack> addLater = new ArrayList<>();
        for (int i = 0; i < this.items.size() ; i++) {
            ItemStack stack = getItem(i);
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
                    setItem(i, CommonUtils.copyStackWithSize(resultStack,compressedCount));
                    addLater.add(CommonUtils.copyStackWithSize(stack,remainderCount));
                }
            }
        }
        sort();

        for (ItemStack itemStack : addLater) {
            ItemStack remainder = itemStack.copy();
            for (int i = 0; i < items.size();i++) {
                remainder = addItem(remainder);
                if (remainder.isEmpty()) break;
            }
            if (!remainder.isEmpty()) {
                player.addItem(remainder);
               // ItemHandlerHelperCommon.giveItemToPlayer(player,remainder);
            }
        }
        sort();
    }

    @Override
    public void setItemDank(int slot, ItemStack stack) {
        setItem(slot, stack);
    }

    @Override
    public ItemStack getItemDank(int slot) {
        return getItem(slot);
    }

    public boolean hasGhostItem(int slot) {
        return !ghostItems.get(slot).isEmpty();
    }

    @Override
    public ItemStack getGhostItem(int slot) {
        return ghostItems.get(slot);
    }

    public void setGhostItem(int slot, Item item) {
        ghostItems.set(slot, new ItemStack(item));
    }

    public void toggleGhostItem(int slot) {
        boolean loc = !ghostItems.get(slot).isEmpty();
        if (!loc) {
            ghostItems.set(slot, CommonUtils.copyStackWithSize(getItem(slot), 1));
        } else {
            ghostItems.set(slot, ItemStack.EMPTY);
        }
        setChanged();
    }

    //0 - 80 are locked slots, 81 is the id, 82 is text color, and 83 is global lock

    @Override
    public int getCount() {
        return getContainerSize() + 3;
    }
}
