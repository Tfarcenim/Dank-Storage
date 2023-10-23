package tfar.dankstorage.container;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import tfar.dankstorage.world.DankInventoryFabric;

public abstract class AbstractDankMenu extends tfar.dankstorage.menu.AbstractDankMenu<DankInventoryFabric> {


    public AbstractDankMenu(MenuType<?> type, int windowId, Inventory playerInventory, DankInventoryFabric dankInventoryFabric) {
        super(type, windowId, playerInventory,dankInventoryFabric);
    }



}
