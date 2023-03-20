package tfar.dankstorage.container;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.MenuType;
import tfar.dankstorage.blockentity.DockBlockEntity;
import tfar.dankstorage.init.ModMenuTypes;
import tfar.dankstorage.utils.DankStats;
import tfar.dankstorage.utils.Utils;
import tfar.dankstorage.world.DankInventory;

public class DockMenu extends AbstractDankMenu {

    private final DockBlockEntity dock;

    public DockMenu(MenuType<?> type, int id, Inventory playerInventory, DankInventory dankInventory, DockBlockEntity dock) {
        super(type, id, playerInventory, dankInventory);
        addDankSlots();
        addPlayerSlots(playerInventory);
        this.dock = dock;
    }

    @Override
    public void setFrequency(int freq) {
        dock.settings.putInt(Utils.ID, freq);
        dock.setChanged();
    }

    @Override
    protected DataSlot getServerPickupData() {
        return new DataSlot() {
            @Override
            public int get() {
                return dock.settings.getInt(Utils.MODE);
            }

            @Override
            public void set(int pValue) {
                dock.settings.putInt(Utils.MODE,pValue);
            }
        };
    }

    public static DockMenu t1(int windowId, Inventory playerInventory) {
        return t1s(windowId, playerInventory,createDummy(DankStats.one),null);
    }

    public static DockMenu t2(int windowId, Inventory playerInventory) {
        return t2s(windowId, playerInventory,createDummy(DankStats.two),null);
    }

    public static DockMenu t3(int windowId, Inventory playerInventory) {
        return t3s(windowId, playerInventory,createDummy(DankStats.three),null);
    }

    public static DockMenu t4(int windowId, Inventory playerInventory) {
        return t4s(windowId, playerInventory,createDummy(DankStats.four),null);
    }

    public static DockMenu t5(int windowId, Inventory playerInventory) {
        return t5s(windowId, playerInventory,createDummy(DankStats.five),null);
    }

    public static DockMenu t6(int windowId, Inventory playerInventory) {
        return t6s(windowId, playerInventory,createDummy(DankStats.six),null);
    }

    public static DockMenu t7(int windowId, Inventory playerInventory) {
        return t7s(windowId, playerInventory, createDummy(DankStats.seven),null);
    }

    static DankInventory createDummy(DankStats stats) {
        return new DankInventory(stats, -1);
    }

    //server
    public static DockMenu t1s(int windowId, Inventory playerInventory, DankInventory inventory,DockBlockEntity dock) {
        return new DockMenu(ModMenuTypes.dank_1_container, windowId, playerInventory, inventory,dock);
    }

    public static DockMenu t2s(int windowId, Inventory playerInventory, DankInventory inventory,DockBlockEntity dock) {
        return new DockMenu(ModMenuTypes.dank_2_container, windowId, playerInventory, inventory,dock);
    }

    public static DockMenu t3s(int windowId, Inventory playerInventory, DankInventory inventory,DockBlockEntity dock) {
        return new DockMenu(ModMenuTypes.dank_3_container, windowId, playerInventory, inventory,dock);
    }

    public static DockMenu t4s(int windowId, Inventory playerInventory, DankInventory inventory,DockBlockEntity dock) {
        return new DockMenu(ModMenuTypes.dank_4_container, windowId, playerInventory, inventory,dock);
    }

    public static DockMenu t5s(int windowId, Inventory playerInventory, DankInventory inventory,DockBlockEntity dock) {
        return new DockMenu(ModMenuTypes.dank_5_container, windowId, playerInventory, inventory,dock);
    }

    public static DockMenu t6s(int windowId, Inventory playerInventory, DankInventory inventory,DockBlockEntity dock) {
        return new DockMenu(ModMenuTypes.dank_6_container, windowId, playerInventory, inventory,dock);
    }

    public static DockMenu t7s(int i, Inventory playerInventory, DankInventory inventory,DockBlockEntity dock) {
        return new DockMenu(ModMenuTypes.dank_7_container, i, playerInventory, inventory,dock);
    }
}

