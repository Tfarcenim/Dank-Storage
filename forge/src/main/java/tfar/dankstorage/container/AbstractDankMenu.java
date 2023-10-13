package tfar.dankstorage.container;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.MenuType;
import tfar.dankstorage.inventory.DankSlot;
import tfar.dankstorage.menu.CAbstractDankMenu;
import tfar.dankstorage.menu.CustomSync;
import tfar.dankstorage.network.DankPacketHandler;
import tfar.dankstorage.utils.PickupMode;
import tfar.dankstorage.world.DankInventory;

import javax.annotation.Nonnull;

public abstract class AbstractDankMenu extends CAbstractDankMenu {

    public DankInventory dankInventory;
    protected final DataSlot pickup;

    public AbstractDankMenu(MenuType<?> type, int windowId, Inventory playerInventory, DankInventory dankInventory) {
        super(type, windowId, dankInventory.dankStats.slots / 9, playerInventory);
        this.dankInventory = dankInventory;
        addDataSlots(dankInventory);
        if (!playerInventory.player.level().isClientSide) {
            setSynchronizer(new CustomSync((ServerPlayer) playerInventory.player));
        }
        pickup = playerInventory.player.level().isClientSide ? DataSlot.standalone(): getServerPickupData();
        addDataSlot(pickup);
    }

    protected abstract DataSlot getServerPickupData();
    public PickupMode getMode() {
        return PickupMode.PICKUP_MODES[pickup.get()];
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

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        //the remote inventory needs to know about locked slots
        for (int i = 0; i < dankInventory.dankStats.slots;i++) {
            DankPacketHandler.sendGhostItemSlot((ServerPlayer) playerInventory.player,containerId,i,dankInventory.getGhostItem(i));
        }
    }
}
