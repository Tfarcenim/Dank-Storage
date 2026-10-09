package tfar.dankstorage.menu;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;
import tfar.dankstorage.inventory.DankInventory;
import tfar.dankstorage.inventory.LockedSlot;
import tfar.dankstorage.item.DankItem;
import tfar.dankstorage.utils.PickupMode;

public abstract class DankHolderMenu extends AbstractContainerMenu {

    private final ContainerData dankInventoryContainerData;
    private final ItemStack bag;//this is always empty on the client
    private final Container container = new SimpleContainer(1);

    public DankHolderMenu(@Nullable MenuType<?> menuType, int id, Inventory inventory, ContainerData inventoryContainerData) {
        this(menuType,id, ItemStack.EMPTY, inventoryContainerData);
    }


    protected DankHolderMenu(@Nullable MenuType<?> menuType, int containerId, ItemStack bag, ContainerData inventoryContainerData) {
        super(menuType, containerId);
        this.bag = bag;
        container.setItem(0,bag);
        this.dankInventoryContainerData = inventoryContainerData;
        addDataSlots(dankInventoryContainerData);
    }

    protected void addDummySlot() {
        Slot slot = new LockedSlot(container,0,-100,-100) {
            @Override
            public boolean isActive() {
                return false;
            }
        };
        addSlot(slot);
    }

    @Override
    public boolean clickMenuButton(Player player, int buttonId) {
        if (buttonId < 0 || buttonId >= ButtonAction.VALUES.length) return false;
        ButtonAction buttonAction = ButtonAction.VALUES[buttonId];
        if (player instanceof ServerPlayer) {
            switch (buttonAction) {
                case LOCK_FREQUENCY -> {
                    toggleFreqLock();
                    return true;
                }
            }
        }
        return super.clickMenuButton(player, buttonId);
    }

    public final ItemStack getBag() {
        return container.getItem(0);
    }

    public PickupMode getMode() {
        return DankItem.getPickupMode(getBag());
    }

    @Override
    public boolean stillValid(Player playerIn) {
        return !bag.isEmpty();
    }

    public int getTextColor() {
        return dankInventoryContainerData.get(DankInventory.TXT_COLOR);
    }

    public void setTextColor(int color) {
        dankInventoryContainerData.set(DankInventory.TXT_COLOR,color);
    }

    public boolean getFreqLock() {
        return dankInventoryContainerData.get(DankInventory.FREQ_LOCK) != 0;
    }

    public void toggleFreqLock() {
        boolean b = getFreqLock();
        dankInventoryContainerData.set(DankInventory.FREQ_LOCK,b ? 0 : 1);
    }

    public ContainerData getDankInventoryContainerData() {
        return dankInventoryContainerData;
    }

    public final int getLinkedFrequency() {
        return DankItem.getFrequency(getBag());
    }

    public void setLinkedFrequency(int frequency) {
        DankItem.setFrequency(bag,frequency);
    }

    public enum ButtonAction {
        LOCK_FREQUENCY, SORT,
        TOGGLE_TAG, TOGGLE_PICKUP,  COMPRESS, CYCLE_SORT_TYPE, TOGGLE_AUTO_SORT;
        static final ButtonAction[] VALUES = values();
    }
}
