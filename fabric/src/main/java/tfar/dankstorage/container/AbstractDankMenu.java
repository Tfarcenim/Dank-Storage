package tfar.dankstorage.container;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import tfar.dankstorage.inventory.DankSlot;
import tfar.dankstorage.menu.CAbstractDankMenu;
import tfar.dankstorage.world.DankInventoryFabric;

import javax.annotation.Nonnull;

public abstract class AbstractDankMenu extends CAbstractDankMenu<DankInventoryFabric> {


    public AbstractDankMenu(MenuType<?> type, int windowId, Inventory playerInventory, DankInventoryFabric dankInventoryFabric) {
        super(type, windowId, dankInventoryFabric.dankStats.slots / 9, playerInventory,dankInventoryFabric);
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
    public boolean isDankSlot(Slot slot) {
        return slot instanceof DankSlot;
    }

    @Override
    public boolean stillValid(@Nonnull Player playerIn) {
        return true;
    }

}
