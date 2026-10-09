package tfar.dankstorage.menu;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import tfar.dankstorage.init.ModMenuTypes;

public class ChangeFrequencyMenu extends DankHolderMenu {

    public ChangeFrequencyMenu(int id, Inventory inventory) {
        this(id,inventory,new SimpleContainerData(DATA_SLOTS),ItemStack.EMPTY);
    }

    public static final int DATA_SLOTS = 3;

    public ChangeFrequencyMenu(int $$1, Inventory inventory, ContainerData dankInventoryContainerData, ItemStack bag) {
        super(ModMenuTypes.change_frequency, $$1,bag, dankInventoryContainerData);
        addDummySlot();
    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        return ItemStack.EMPTY;
    }

}
