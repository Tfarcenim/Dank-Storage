package tfar.dankstorage.menu;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import tfar.dankstorage.init.ModMenuTypes;
import tfar.dankstorage.inventory.DankInterface;

public class ChangeFrequencyMenu extends AbstractContainerMenu {

    private final ContainerData containerData;
    private final DataSlot currentTier;

    public ChangeFrequencyMenu(int id, Inventory inventory) {
        this(id,inventory,new SimpleContainerData(3),DataSlot.standalone());
    }

    public ChangeFrequencyMenu(int $$1, Inventory inventory, ContainerData containerData, DataSlot currentTier) {
        super(ModMenuTypes.change_frequency, $$1);
        this.containerData = containerData;
        this.currentTier = currentTier;
        //addDataSlots(dankInventory);
        addDataSlots(containerData);
        addDataSlot(currentTier);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        return ItemStack.EMPTY;
    }

    public int getFrequency() {
        return containerData.get(DankInterface.FREQ);
    }

    public int getTextColor() {
        return containerData.get(DankInterface.TXT_COLOR);
    }

    public boolean getFreqLock() {
        return containerData.get(DankInterface.FREQ_LOCK) != 0;
    }

    public void toggleFreqLock() {
        boolean b = getFreqLock();
        containerData.set(DankInterface.FREQ_LOCK,b ? 0 : 1);
    }

    public int getCurrentTier() {
        return currentTier.get();
    }

    public ContainerData getContainerData() {
        return containerData;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    public void setLinkedFrequency(int frequency) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id < 0 || id >= AbstractDankMenu.ButtonAction.VALUES.length) return false;
        AbstractDankMenu.ButtonAction buttonAction = AbstractDankMenu.ButtonAction.VALUES[id];
        if (player instanceof ServerPlayer serverPlayer) {
            switch (buttonAction) {
                case LOCK_FREQUENCY -> toggleFreqLock();
              //  case SORT -> dankInventory.sort();
              //  case COMPRESS -> dankInventory.compress(serverPlayer);
             //   case TOGGLE_TAG -> CommonUtils.toggleTagMode(serverPlayer);
            //    case TOGGLE_PICKUP -> CommonUtils.togglePickupMode(serverPlayer);
            }
        }
        return false;
    }

}
