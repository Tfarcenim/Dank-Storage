package tfar.dankstorage.menu;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import tfar.dankstorage.inventory.DankInterface;
import tfar.dankstorage.inventory.LockedSlot;
import tfar.dankstorage.network.PacketIds;
import tfar.dankstorage.network.client.S2CSendGhostSlotPacket;
import tfar.dankstorage.platform.Services;
import tfar.dankstorage.utils.CommonUtils;
import tfar.dankstorage.utils.DankStats;
import tfar.dankstorage.utils.PickupMode;

import javax.annotation.Nonnull;

public abstract class AbstractDankMenu extends AbstractContainerMenu {

    public final Inventory playerInventory;
    public final int rows;
    public final DankInterface dankInventory;
    protected final DataSlot pickup;

    public PickupMode getMode() {
        return PickupMode.VALUES[pickup.get()];
    }

    public enum ButtonAction {
        LOCK_FREQUENCY, SORT,
        TOGGLE_TAG, TOGGLE_PICKUP,  COMPRESS;
        private static final ButtonAction[] VALUES = values();
    }


    public AbstractDankMenu(MenuType<?> type, int windowId, Inventory playerInventory, DankInterface dankInventory) {
        super(type, windowId);
        this.playerInventory = playerInventory;
        this.dankInventory = dankInventory;
        this.rows = dankInventory.getContainerSizeDank() /9;
        addDataSlots(dankInventory);
        if (!playerInventory.player.level().isClientSide) {
            setSynchronizer(new CustomSync((ServerPlayer) playerInventory.player));
        }
        pickup = playerInventory.player.level().isClientSide ? DataSlot.standalone(): getServerPickupData();
        addDataSlot(pickup);
    }

    static DankInterface createDummy(DankStats stats) {
        return Services.PLATFORM.createInventory(stats, CommonUtils.INVALID);
    }

    protected abstract DataSlot getServerPickupData();

    protected void addPlayerSlots(Inventory playerinventory, int locked) {
        int yStart = 32 + 18 * rows;
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                int x = 8 + col * 18;
                int y = row * 18 + yStart;
                this.addSlot(new Slot(playerinventory, col + row * 9 + 9, x, y));
            }
        }

        for (int row = 0; row < 9; ++row) {
            int x = 8 + row * 18;
            int y = yStart + 58;
            if (row != locked)
                this.addSlot(new Slot(playerinventory, row, x, y));
            else
                this.addSlot(new LockedSlot(playerinventory, row, x, y));
        }
    }

    @Override
    public void doClick(int pSlotId, int pButton, ClickType pClickType, Player pPlayer) {
        if (pClickType != ClickType.SWAP)
            super.doClick(pSlotId, pButton, pClickType, pPlayer);
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id < 0 || id >= ButtonAction.VALUES.length) return false;
        ButtonAction buttonAction = ButtonAction.VALUES[id];
        if (player instanceof ServerPlayer serverPlayer) {
            switch (buttonAction) {
                case LOCK_FREQUENCY -> dankInventory.toggleFrequencyLock();
                case SORT -> dankInventory.sort();
                case COMPRESS -> dankInventory.compress(serverPlayer);
                case TOGGLE_TAG -> CommonUtils.toggleTagMode(serverPlayer);
                case TOGGLE_PICKUP -> CommonUtils.togglePickupMode(serverPlayer);
            }
        }
        return true;
    }

    @Nonnull
    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            itemstack = slotStack.copy();


            if (index < rows * 9) {
                if (!this.moveItemStackTo(slotStack, rows * 9, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(slotStack, 0, rows * 9, false)) {
                return ItemStack.EMPTY;
            }

            if (slotStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return itemstack;
    }

    protected void addDankSlots() {
        int slotIndex = 0;
        for (int row = 0; row < rows; ++row) {
            for (int col = 0; col < 9; ++col) {
                int x = 8 + col * 18;
                int y = row * 18 + 18;
                this.addSlot(Services.PLATFORM.createSlot(dankInventory, slotIndex, x, y));
                slotIndex++;
            }
        }
    }

    @Override
    public boolean stillValid(@Nonnull Player playerIn) {
        return true;
    }


    //used by quick transfer, needs to respect locked slots
    @Override
    protected boolean moveItemStackTo(ItemStack stack, int startIndex, int endIndex, boolean reverse) {
        boolean didSomething = false;
        int i = startIndex;

        if (reverse) {
            i = endIndex - 1;
        }

        while (!stack.isEmpty()) {
            if (reverse) {
                if (i < startIndex) break;
            } else {
                if (i >= endIndex) break;
            }

            Slot slot = this.slots.get(i);
            ItemStack slotStack = slot.getItem();

            if (!slotStack.isEmpty() && slotStack.getItem() == stack.getItem() && ItemStack.isSameItemSameTags(stack, slotStack)) {
                int combinedCount = slotStack.getCount() + stack.getCount();
                int maxSize = slot.getMaxStackSize(slotStack);

                if (combinedCount <= maxSize) {
                    stack.setCount(0);
                    slotStack.setCount(combinedCount);
                    slot.setChanged();
                    didSomething = true;
                } else if (slotStack.getCount() < maxSize) {
                    stack.shrink(maxSize - slotStack.getCount());
                    slotStack.setCount(maxSize);
                    slot.setChanged();
                    didSomething = true;
                }
            }

            i += reverse ? -1 : 1;
        }

        if (!stack.isEmpty()) {
            if (reverse) i = endIndex - 1;
            else i = startIndex;

            while (true) {
                if (reverse) {
                    if (i < startIndex) break;
                } else {
                    if (i >= endIndex) break;
                }

                Slot slot = this.slots.get(i);
                ItemStack itemstack1 = slot.getItem();

                if (itemstack1.isEmpty() && slot.mayPlace(stack)) {
                    if (stack.getCount() > slot.getMaxStackSize(stack)) {
                        slot.set(stack.split(slot.getMaxStackSize(stack)));
                    } else {
                        slot.set(stack.split(stack.getCount()));
                    }

                    slot.setChanged();
                    didSomething = true;
                    break;
                }

                i += reverse ? -1 : 1;
            }
        }

        return didSomething;
    }

    public boolean isDankSlot(Slot slot) {
        return slot.getClass().getName().endsWith("DankSlot");
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        //the remote inventory needs to know about locked slots
        for (int i = 0; i < dankInventory.getDankStats().slots; i++) {
            Services.PLATFORM.sendToClient(new S2CSendGhostSlotPacket(containerId,i, dankInventory.getGhostItem(i)), PacketIds.sync_ghost_slot,(ServerPlayer)
                    playerInventory.player);
        }
    }

    public abstract void setFrequency(int freq);
}
