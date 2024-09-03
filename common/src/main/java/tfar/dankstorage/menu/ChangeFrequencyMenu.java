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
import tfar.dankstorage.inventory.DankInventory;
import tfar.dankstorage.item.DankItem;

public class ChangeFrequencyMenu extends AbstractContainerMenu {

    private final ContainerData containerData;
    private final DataSlot currentTier;
    private final ItemStack bag;


    public ChangeFrequencyMenu(int id, Inventory inventory) {
        this(id,inventory,new SimpleContainerData(2),DataSlot.standalone(),ItemStack.EMPTY);
    }

    public ChangeFrequencyMenu(int $$1, Inventory inventory, ContainerData containerData, DataSlot currentTier,ItemStack bag) {
        super(ModMenuTypes.change_frequency, $$1);
        this.containerData = containerData;
        this.currentTier = currentTier;
        this.bag = bag;
        //addDataSlots(dankInventory);
        addDataSlots(containerData);
        addDataSlot(currentTier);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        return ItemStack.EMPTY;
    }

    public int getFrequency() {
        return DankItem.getFrequency(bag);
    }

    public int getTextColor() {
        return containerData.get(DankInventory.TXT_COLOR);
    }

    public boolean getFreqLock() {
        return containerData.get(DankInventory.FREQ_LOCK) != 0;
    }

    public void toggleFreqLock() {
        boolean b = getFreqLock();
        containerData.set(DankInventory.FREQ_LOCK,b ? 0 : 1);
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
        DankItem.setFrequency(bag,frequency);
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id < 0 || id >= DankMenu.ButtonAction.VALUES.length) return false;
        DankMenu.ButtonAction buttonAction = DankMenu.ButtonAction.VALUES[id];
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
