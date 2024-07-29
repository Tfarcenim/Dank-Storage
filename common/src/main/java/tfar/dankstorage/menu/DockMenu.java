package tfar.dankstorage.menu;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.MenuType;
import tfar.dankstorage.blockentity.CommonDockBlockEntity;
import tfar.dankstorage.init.ModMenuTypes;
import tfar.dankstorage.inventory.DankInterface;
import tfar.dankstorage.utils.CommonUtils;
import tfar.dankstorage.utils.DankStats;
import tfar.dankstorage.utils.PickupMode;

public class DockMenu extends AbstractDankMenu {

    private final CommonDockBlockEntity dock;

    public DockMenu(MenuType<?> type, int id, Inventory playerInventory, DankInterface dankInventory, CommonDockBlockEntity dock) {
        super(type, id, playerInventory,dankInventory);
        addDankSlots();
        addPlayerSlots(playerInventory,-1);
        this.dock = dock;
    }

    @Override
    public void setFrequency(int freq) {
        dock.setFrequency(freq);
        dock.setChanged();
    }

    @Override
    protected DataSlot getServerPickupData() {
        return new DataSlot() {
            @Override
            public int get() {
                return dock.pickupMode.ordinal();
            }

            @Override
            public void set(int pValue) {
                dock.pickupMode = PickupMode.VALUES[pValue];
            }
        };
    }

    public static DockMenu t1(int windowId, Inventory playerInventory) {
        return t1s(windowId, playerInventory, DankInterface.createDummy(DankStats.one),null);
    }

    public static DockMenu t2(int windowId, Inventory playerInventory) {
        return t2s(windowId, playerInventory, DankInterface.createDummy(DankStats.two),null);
    }

    public static DockMenu t3(int windowId, Inventory playerInventory) {
        return t3s(windowId, playerInventory, DankInterface.createDummy(DankStats.three),null);
    }

    public static DockMenu t4(int windowId, Inventory playerInventory) {
        return t4s(windowId, playerInventory, DankInterface.createDummy(DankStats.four),null);
    }

    public static DockMenu t5(int windowId, Inventory playerInventory) {
        return t5s(windowId, playerInventory, DankInterface.createDummy(DankStats.five),null);
    }

    public static DockMenu t6(int windowId, Inventory playerInventory) {
        return t6s(windowId, playerInventory, DankInterface.createDummy(DankStats.six),null);
    }

    public static DockMenu t7(int windowId, Inventory playerInventory) {
        return t7s(windowId, playerInventory, DankInterface.createDummy(DankStats.seven),null);
    }



    //server
    public static DockMenu t1s(int windowId, Inventory playerInventory, DankInterface inventory, CommonDockBlockEntity dock) {
        return new DockMenu(ModMenuTypes.dank_1, windowId, playerInventory, inventory,dock);
    }

    public static DockMenu t2s(int windowId, Inventory playerInventory, DankInterface inventory, CommonDockBlockEntity dock) {
        return new DockMenu(ModMenuTypes.dank_2, windowId, playerInventory, inventory,dock);
    }

    public static DockMenu t3s(int windowId, Inventory playerInventory, DankInterface inventory, CommonDockBlockEntity dock) {
        return new DockMenu(ModMenuTypes.dank_3, windowId, playerInventory, inventory,dock);
    }

    public static DockMenu t4s(int windowId, Inventory playerInventory, DankInterface inventory, CommonDockBlockEntity dock) {
        return new DockMenu(ModMenuTypes.dank_4, windowId, playerInventory, inventory,dock);
    }

    public static DockMenu t5s(int windowId, Inventory playerInventory, DankInterface inventory, CommonDockBlockEntity dock) {
        return new DockMenu(ModMenuTypes.dank_5, windowId, playerInventory, inventory,dock);
    }

    public static DockMenu t6s(int windowId, Inventory playerInventory, DankInterface inventory, CommonDockBlockEntity dock) {
        return new DockMenu(ModMenuTypes.dank_6, windowId, playerInventory, inventory,dock);
    }

    public static DockMenu t7s(int i, Inventory playerInventory, DankInterface inventory, CommonDockBlockEntity dock) {
        return new DockMenu(ModMenuTypes.dank_7, i, playerInventory, inventory,dock);
    }
}

