package tfar.dankstorage.menu;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;
import tfar.dankstorage.utils.CommonUtils;

public class ChangeFrequencyMenuItem extends ChangeFrequencyMenu {

    private final ItemStack bag;

    public ChangeFrequencyMenuItem(int $$1, Inventory inventory, ContainerData containerData, DataSlot currentTier, ItemStack bag) {
        super($$1, inventory, containerData, currentTier);
        this.bag = bag;
    }

    @Override
    public void setLinkedFrequency(int frequency) {
        CommonUtils.setFrequency(bag,frequency);
    }
}
