package tfar.dankstorage.menu;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import tfar.dankstorage.init.ModMenuTypes;
import tfar.dankstorage.inventory.DankInventory;
import tfar.dankstorage.inventory.DankSlot;
import tfar.dankstorage.inventory.LockedSlot;
import tfar.dankstorage.item.DankItem;
import tfar.dankstorage.network.client.S2CSendGhostSlotPacket;
import tfar.dankstorage.platform.Services;
import tfar.dankstorage.utils.CommonUtils;
import tfar.dankstorage.utils.DankStats;
import tfar.dankstorage.utils.PickupMode;

import javax.annotation.Nonnull;

public class DankMenu extends AbstractContainerMenu {

    public final Inventory playerInventory;
    public final int rows;
    public final DankInventory dankInventory;
    public final ItemStack bag;

    final Container container = new SimpleContainer(1);


    public enum ButtonAction {
        LOCK_FREQUENCY, SORT,
        TOGGLE_TAG, TOGGLE_PICKUP,  COMPRESS, CYCLE_SORT_TYPE, TOGGLE_AUTO_SORT;
        static final ButtonAction[] VALUES = values();
    }


    public DankMenu(MenuType<?> type, int windowId, Inventory playerInventory, DankInventory dankInventory,ItemStack bag) {
        super(type, windowId);
        this.playerInventory = playerInventory;
        this.dankInventory = dankInventory;
        this.rows = dankInventory.slotCount() /9;
        this.bag = bag;
        container.setItem(0,bag);

        addDankSlots();
        addPlayerSlots(playerInventory,-1);
        Slot slot = new LockedSlot(container,0,-100,-100) {
            @Override
            public boolean isActive() {
                return false;
            }
        };
        addSlot(slot);

        addDataSlots(dankInventory);
    }

    public ItemStack getBag() {
        return container.getItem(0);
    }

    public PickupMode getMode() {
        return DankItem.getPickupMode(bag);
    }


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
                case COMPRESS -> dankInventory.compress(serverPlayer.serverLevel(), serverPlayer);
                case TOGGLE_TAG -> CommonUtils.toggleTagMode(serverPlayer);
                case TOGGLE_PICKUP -> CommonUtils.togglePickupMode(serverPlayer);
                case CYCLE_SORT_TYPE -> {
                    DankInventory dankInventory = DankItem.getInventoryFrom(bag, serverPlayer.server);
                    if (dankInventory != null) {
                        dankInventory.setSortingType(DankItem.cycle(dankInventory.getSortingType()));
                        //needed to force syncing
                        for (int i = 0; i < remoteSlots.size(); i++) {
                            remoteSlots.set(i,ItemStack.EMPTY);
                        }
                    }
                }
                case TOGGLE_AUTO_SORT -> {
                    DankInventory dankInventory = DankItem.getInventoryFrom(bag, serverPlayer.server);
                    if (dankInventory != null) {
                        dankInventory.toggleAutoSort();
                    }
                }
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
                this.addSlot(new DankSlot(dankInventory, slotIndex, x, y));
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

            if (!slotStack.isEmpty() && slotStack.getItem() == stack.getItem() && ItemStack.isSameItemSameComponents(stack, slotStack)) {
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
        for (int i = 0; i < dankInventory.items.size(); i++) {
            Services.PLATFORM.sendToClient(new S2CSendGhostSlotPacket(containerId,i, dankInventory.getGhostItem(i)), (ServerPlayer)
                    playerInventory.player);
        }
    }

    public void setFrequency(int freq) {
        DankItem.setFrequency(bag,freq);
    }


    public static DankMenu t1(int id, Inventory inv) {
        return t1s(id, inv,  DankInventory.createDummy(DankStats.one), ItemStack.EMPTY);
    }

    public static DankMenu t2(int id, Inventory inv) {
        return t2s(id, inv,  DankInventory.createDummy(DankStats.two), ItemStack.EMPTY);
    }

    public static DankMenu t3(int id, Inventory inv) {
        return t3s(id, inv,  DankInventory.createDummy(DankStats.three), ItemStack.EMPTY);
    }

    public static DankMenu t4(int id, Inventory inv) {
        return t4s( id, inv,  DankInventory.createDummy(DankStats.four), ItemStack.EMPTY);
    }

    public static DankMenu t5(int id, Inventory inv) {
        return t5s( id, inv,  DankInventory.createDummy(DankStats.five), ItemStack.EMPTY);
    }

    public static DankMenu t6(int id, Inventory inv) {
        return t6s(id, inv,  DankInventory.createDummy(DankStats.six), ItemStack.EMPTY);
    }

    public static DankMenu t7(int id, Inventory inv) {
        return t7s(id, inv, DankInventory.createDummy(DankStats.seven), ItemStack.EMPTY);
    }

    public static DankMenu t1s(int id, Inventory inv, DankInventory dankInventory, ItemStack stack) {
        return new DankMenu(ModMenuTypes.dank_1, id, inv, dankInventory,stack);
    }

    public static DankMenu t2s(int id, Inventory inv, DankInventory dankInventory, ItemStack stack) {
        return new DankMenu(ModMenuTypes.dank_2, id, inv, dankInventory,stack);
    }

    public static DankMenu t3s(int id, Inventory inv, DankInventory dankInventory, ItemStack stack) {
        return new DankMenu(ModMenuTypes.dank_3, id, inv, dankInventory,stack);
    }

    public static DankMenu t4s(int id, Inventory inv, DankInventory dankInventory, ItemStack stack) {
        return new DankMenu(ModMenuTypes.dank_4, id, inv, dankInventory,stack);
    }

    public static DankMenu t5s(int id, Inventory inv, DankInventory dankInventory, ItemStack stack) {
        return new DankMenu(ModMenuTypes.dank_5, id, inv, dankInventory,stack);
    }

    public static DankMenu t6s(int id, Inventory inv, DankInventory dankInventory, ItemStack stack) {
        return new DankMenu(ModMenuTypes.dank_6, id, inv, dankInventory,stack);
    }

    public static DankMenu t7s(int id, Inventory inv, DankInventory dankInventory, ItemStack stack) {
        return new DankMenu(ModMenuTypes.dank_7, id, inv, dankInventory,stack);
    }
    
}
