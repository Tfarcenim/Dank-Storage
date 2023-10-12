package tfar.dankstorage.container;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import tfar.dankstorage.inventory.DankSlot;
import tfar.dankstorage.network.DankPacketHandler;
import tfar.dankstorage.world.DankInventory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public abstract class AbstractDankMenu extends AbstractContainerMenu {

    public final int rows;
    public final Inventory playerInventory;
    public DankInventory dankInventory;


    public AbstractDankMenu(MenuType<?> type, int windowId, Inventory playerInventory, int rows, DankInventory dankInventory) {
        super(type, windowId);
        this.rows = rows;
        this.playerInventory = playerInventory;
        this.dankInventory = dankInventory;
        addDataSlots(dankInventory);
    }

    public static boolean canItemQuickReplace(@Nullable Slot slot, @Nonnull ItemStack stack, boolean stackSizeMatters) {
        boolean flag = slot == null || !slot.hasItem();
        if (slot != null) {
            ItemStack slotStack = slot.getItem();

            if (!flag && stack.sameItem(slotStack) && ItemStack.tagMatches(slotStack, stack)) {
                return slotStack.getCount() + (stackSizeMatters ? 0 : stack.getCount()) <= slot.getMaxStackSize(slotStack);
            }
        }
        return flag;
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

    protected void addPlayerSlots(Inventory playerinventory) {
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
            this.addSlot(new Slot(playerinventory, row, x, y));
        }
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

    @Override
    public void doClick(int slotId, int dragType, ClickType clickType, Player player) {
        Inventory inventory = player.getInventory();
        if (clickType == ClickType.QUICK_CRAFT) {
            int i = this.quickcraftStatus;
            this.quickcraftStatus = getQuickcraftHeader(dragType);
            if ((i != 1 || this.quickcraftStatus != 2) && i != this.quickcraftStatus) {
                this.resetQuickCraft();
            } else if (this.getCarried().isEmpty()) {
                this.resetQuickCraft();
            } else if (this.quickcraftStatus == 0) {
                this.quickcraftType = getQuickcraftType(dragType);
                if (isValidQuickcraftType(this.quickcraftType, player)) {
                    this.quickcraftStatus = 1;
                    this.quickcraftSlots.clear();
                } else {
                    this.resetQuickCraft();
                }
            } else if (this.quickcraftStatus == 1) {
                Slot slot = this.slots.get(slotId);
                ItemStack itemstack = this.getCarried();
                if (canItemQuickReplace(slot, itemstack, true) && slot.mayPlace(itemstack) && (this.quickcraftType == 2 || itemstack.getCount() > this.quickcraftSlots.size()) && this.canDragTo(slot)) {
                    this.quickcraftSlots.add(slot);
                }
            } else if (this.quickcraftStatus == 2) {
                if (!this.quickcraftSlots.isEmpty()) {
                    if (this.quickcraftSlots.size() == 1) {
                        int l = this.quickcraftSlots.iterator().next().index;
                        this.resetQuickCraft();
                        this.doClick(l, this.quickcraftType, ClickType.PICKUP, player);
                        return;
                    }

                    ItemStack itemstack3 = this.getCarried().copy();
                    int j1 = this.getCarried().getCount();

                    for(Slot slot1 : this.quickcraftSlots) {
                        ItemStack itemstack1 = this.getCarried();
                        if (slot1 != null && canItemQuickReplace(slot1, itemstack1, true) && slot1.mayPlace(itemstack1) && (this.quickcraftType == 2 || itemstack1.getCount() >= this.quickcraftSlots.size()) && this.canDragTo(slot1)) {
                            ItemStack itemstack2 = itemstack3.copy();
                            int j = slot1.hasItem() ? slot1.getItem().getCount() : 0;
                            getQuickCraftSlotCount(this.quickcraftSlots, this.quickcraftType, itemstack2, j);
                            int k = Math.min(itemstack2.getMaxStackSize(), slot1.getMaxStackSize(itemstack2));
                            if (itemstack2.getCount() > k) {
                                itemstack2.setCount(k);
                            }

                            j1 -= itemstack2.getCount() - j;
                            slot1.set(itemstack2);
                        }
                    }

                    itemstack3.setCount(j1);
                    this.setCarried(itemstack3);
                }

                this.resetQuickCraft();
            } else {
                this.resetQuickCraft();
            }
        } else if (this.quickcraftStatus != 0) {
            this.resetQuickCraft();
        } else if ((clickType == ClickType.PICKUP || clickType == ClickType.QUICK_MOVE) && (dragType == 0 || dragType == 1)) {
            ClickAction clickaction = dragType == 0 ? ClickAction.PRIMARY : ClickAction.SECONDARY;
            if (slotId == SLOT_CLICKED_OUTSIDE) {
                if (!this.getCarried().isEmpty()) {
                    if (clickaction == ClickAction.PRIMARY) {
                        player.drop(this.getCarried(), true);
                        this.setCarried(ItemStack.EMPTY);
                    } else {
                        player.drop(this.getCarried().split(1), true);
                    }
                }
            } else if (clickType == ClickType.QUICK_MOVE) {
                if (slotId < 0) {
                    return;
                }

                Slot slot6 = this.slots.get(slotId);
                if (!slot6.mayPickup(player)) {
                    return;
                }

                for(ItemStack itemstack9 = this.quickMoveStack(player, slotId); !itemstack9.isEmpty() && ItemStack.isSame(slot6.getItem(), itemstack9); itemstack9 = this.quickMoveStack(player, slotId)) {
                }
            } else {
                if (slotId < 0) {
                    return;
                }

                Slot slot7 = this.slots.get(slotId);
                ItemStack itemstack10 = slot7.getItem();
                ItemStack carried = this.getCarried();
                player.updateTutorialInventoryAction(carried, slot7.getItem(), clickaction);
                if (!carried.overrideStackedOnOther(slot7, clickaction, player) && !itemstack10.overrideOtherStackedOnMe(carried, slot7, clickaction, player, this.createCarriedSlotAccess())) {
                    if (itemstack10.isEmpty()) {
                        if (!carried.isEmpty()) {
                            int l2 = clickaction == ClickAction.PRIMARY ? carried.getCount() : 1;
                            this.setCarried(slot7.safeInsert(carried, l2));
                        }
                    } else if (slot7.mayPickup(player)) {
                        if (carried.isEmpty()) {
                            int max = itemstack10.getMaxStackSize();
                            int i3 = clickaction == ClickAction.PRIMARY ? max : (max + 1) / 2;//restrict extraction to max stack size
                            Optional<ItemStack> optional1 = slot7.tryRemove(i3, Integer.MAX_VALUE, player);
                            optional1.ifPresent(stack -> {
                                this.setCarried(stack);
                                slot7.onTake(player, stack);
                            });

                        } else if (slot7.mayPlace(carried)) {
                            if (ItemStack.isSameItemSameTags(itemstack10, carried)) {
                                int j3 = clickaction == ClickAction.PRIMARY ? carried.getCount() : 1;
                                this.setCarried(slot7.safeInsert(carried, j3));
                            } else if (carried.getCount() <= slot7.getMaxStackSize(carried)) {
                                slot7.set(carried);
                                this.setCarried(itemstack10);
                            }
                        } else if (ItemStack.isSameItemSameTags(itemstack10, carried)) {
                            Optional<ItemStack> optional = slot7.tryRemove(itemstack10.getCount(), carried.getMaxStackSize() - carried.getCount(), player);
                            optional.ifPresent(stack -> {
                                carried.grow(stack.getCount());
                                slot7.onTake(player, stack);
                            });
                        }
                    }
                }

                slot7.setChanged();
            }
        } else if (clickType == ClickType.SWAP) {
       /*     Slot slot2 = this.slots.get(slotId);
            ItemStack itemstack4 = inventory.getItem(dragType);
            ItemStack itemstack7 = slot2.getItem();
            if (!itemstack4.isEmpty() || !itemstack7.isEmpty()) {
                if (itemstack4.isEmpty()) {
                    if (slot2.mayPickup(player)) {
                        inventory.setItem(dragType, itemstack7);
                        slot2.onSwapCraft(itemstack7.getCount());
                        slot2.set(ItemStack.EMPTY);
                        slot2.onTake(player, itemstack7);
                    }
                } else if (itemstack7.isEmpty()) {
                    if (slot2.mayPlace(itemstack4)) {
                        int l1 = slot2.getMaxStackSize(itemstack4);
                        if (itemstack4.getCount() > l1) {
                            slot2.set(itemstack4.split(l1));
                        } else {
                            inventory.setItem(dragType, ItemStack.EMPTY);
                            slot2.set(itemstack4);
                        }
                    }
                } else if (slot2.mayPickup(player) && slot2.mayPlace(itemstack4)) {
                    int i2 = slot2.getMaxStackSize(itemstack4);
                    if (itemstack4.getCount() > i2) {
                        slot2.set(itemstack4.split(i2));
                        slot2.onTake(player, itemstack7);
                        if (!inventory.add(itemstack7)) {
                            player.drop(itemstack7, true);
                        }
                    } else {
                        inventory.setItem(dragType, itemstack7);
                        slot2.set(itemstack4);
                        slot2.onTake(player, itemstack7);
                    }
                }
            }*/
        } else if (clickType == ClickType.CLONE && player.getAbilities().instabuild && this.getCarried().isEmpty() && slotId >= 0) {
            Slot slot5 = this.slots.get(slotId);
            if (slot5.hasItem()) {
                ItemStack itemstack6 = slot5.getItem().copy();
                itemstack6.setCount(itemstack6.getMaxStackSize());
                this.setCarried(itemstack6);
            }
        } else if (clickType == ClickType.THROW && this.getCarried().isEmpty() && slotId >= 0) {
            Slot slot4 = this.slots.get(slotId);
            int i1 = dragType == 0 ? 1 : slot4.getItem().getCount();
            ItemStack itemstack8 = slot4.safeTake(i1, Integer.MAX_VALUE, player);
            player.drop(itemstack8, true);
        } else if (clickType == ClickType.PICKUP_ALL && slotId >= 0) {
            Slot slot3 = this.slots.get(slotId);
            ItemStack itemstack5 = this.getCarried();
            if (!itemstack5.isEmpty() && (!slot3.hasItem() || !slot3.mayPickup(player))) {
                int k1 = dragType == 0 ? 0 : this.slots.size() - 1;
                int j2 = dragType == 0 ? 1 : -1;

                for(int k2 = 0; k2 < 2; ++k2) {
                    for(int k3 = k1; k3 >= 0 && k3 < this.slots.size() && itemstack5.getCount() < itemstack5.getMaxStackSize(); k3 += j2) {
                        Slot slot8 = this.slots.get(k3);
                        if (slot8.hasItem() && canItemQuickReplace(slot8, itemstack5, true) && slot8.mayPickup(player) && this.canTakeItemForPickAll(itemstack5, slot8)) {
                            ItemStack itemstack12 = slot8.getItem();
                            if (k2 != 0 || itemstack12.getCount() != itemstack12.getMaxStackSize()) {
                                ItemStack itemstack13 = slot8.safeTake(itemstack12.getCount(), itemstack5.getMaxStackSize() - itemstack5.getCount(), player);
                                itemstack5.grow(itemstack13.getCount());
                            }
                        }
                    }
                }
            }
        }
    }

    private SlotAccess createCarriedSlotAccess() {
        return new SlotAccess(){

            @Override
            public ItemStack get() {
                return getCarried();
            }

            @Override
            public boolean set(ItemStack itemStack) {
                setCarried(itemStack);
                return true;
            }
        };
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

            if (!slotStack.isEmpty() && slotStack.getItem() == stack.getItem() && ItemStack.tagMatches(stack, slotStack)) {
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

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        //the remote inventory needs to know about locked slots
        for (int i = 0; i < dankInventory.dankStats.slots;i++) {
            DankPacketHandler.sendGhostItem((ServerPlayer) playerInventory.player,containerId,i,dankInventory.getGhostItem(i));
        }
    }

    public abstract void setFrequency(int freq);
}
