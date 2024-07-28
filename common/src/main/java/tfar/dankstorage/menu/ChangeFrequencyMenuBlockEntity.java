package tfar.dankstorage.menu;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.DataSlot;
import tfar.dankstorage.blockentity.CommonDockBlockEntity;
import tfar.dankstorage.utils.CommonUtils;

public class ChangeFrequencyMenuBlockEntity extends ChangeFrequencyMenu {

    private final CommonDockBlockEntity<?> dock;

    public ChangeFrequencyMenuBlockEntity(int $$1, Inventory inventory, ContainerData containerData, DataSlot currentTier,CommonDockBlockEntity<?> dock) {
        super($$1, inventory, containerData, currentTier);
        this.dock = dock;
    }

    @Override
    public void setLinkedFrequency(int frequency) {
        dock.settings.putInt(CommonUtils.FREQ, frequency);
        dock.setChanged();
    }
}
