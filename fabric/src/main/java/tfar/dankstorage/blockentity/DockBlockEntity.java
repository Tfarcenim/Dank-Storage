
package tfar.dankstorage.blockentity;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import tfar.dankstorage.DankStorageFabric;
import tfar.dankstorage.block.CDockBlock;
import tfar.dankstorage.container.DockMenu;
import tfar.dankstorage.inventory.api.DankInventorySlotWrapper;
import tfar.dankstorage.utils.DankStats;
import tfar.dankstorage.utils.Utils;
import tfar.dankstorage.world.DankInventoryFabric;
import tfar.dankstorage.world.DankSavedData;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class DockBlockEntity extends CommonDockBlockEntity implements MenuProvider {

    public DockBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(DankStorageFabric.dank_tile, blockPos, blockState);
    }

    public static final DankInventoryFabric DUMMY = new DankInventoryFabric(DankStats.zero, Utils.INVALID);

    public DankInventoryFabric getInventory() {
        if (settings != null && settings.contains(Utils.FREQ)) {
            int frequency = settings.getInt(Utils.FREQ);
            DankSavedData savedData = DankStorageFabric.getData(frequency,level.getServer());
            DankInventoryFabric dankInventoryFabric = savedData.createInventory(frequency);

            if (!dankInventoryFabric.valid()) {
                savedData.setStats(DankStats.values()[getBlockState().getValue(CDockBlock.TIER)],frequency);
                dankInventoryFabric = savedData.createInventory(frequency);
            }

            return dankInventoryFabric;
        }
        return DUMMY;
    }

    public int getComparatorSignal() {
        return this.getInventory().calcRedstone();
    }

    @Nullable
    @Override
    public DockMenu createMenu(int syncId, Inventory inventory, Player player) {

        int tier = getBlockState().getValue(CDockBlock.TIER);

        DankInventoryFabric dankInventoryFabric = getInventory();

        DankStats type = DankStats.values()[tier];
        if (type != dankInventoryFabric.dankStats) {
            if (type.ordinal() < dankInventoryFabric.dankStats.ordinal()) {
                Utils.warn(player, type, dankInventoryFabric.dankStats);
                return null;
            }
            dankInventoryFabric.upgradeTo(type);
        }

        return switch (getBlockState().getValue(CDockBlock.TIER)) {
            case 1 -> DockMenu.t1s(syncId, inventory, dankInventoryFabric, this);
            case 2 -> DockMenu.t2s(syncId, inventory, dankInventoryFabric, this);
            case 3 -> DockMenu.t3s(syncId, inventory, dankInventoryFabric, this);
            case 4 -> DockMenu.t4s(syncId, inventory, dankInventoryFabric, this);
            case 5 -> DockMenu.t5s(syncId, inventory, dankInventoryFabric, this);
            case 6 -> DockMenu.t6s(syncId, inventory, dankInventoryFabric, this);
            case 7 -> DockMenu.t7s(syncId, inventory, dankInventoryFabric, this);
            default -> null;
        };
    }

    public void upgradeTo(DankStats stats) {
        level.setBlockAndUpdate(worldPosition, getBlockState().setValue(CDockBlock.TIER, stats.ordinal()));
        DankInventoryFabric dankInventoryFabric = getInventory();
        dankInventoryFabric.upgradeTo(stats);
    }

    //item api

    private CombinedStorage<ItemVariant,DankInventorySlotWrapper> storage;

    public CombinedStorage<ItemVariant,DankInventorySlotWrapper> getStorage(Direction direction) {

        DankInventoryFabric dankInventoryFabric = getInventory();

        if (storage != null && storage.parts.size() != dankInventoryFabric.getContainerSize()) {
            storage = null;
        }
        if (storage == null) {
            storage = create(dankInventoryFabric);
        }
        return storage;
    }


    public static CombinedStorage<ItemVariant,DankInventorySlotWrapper> create(DankInventoryFabric dankInventoryFabric) {
        int slots = dankInventoryFabric.getContainerSize();

        List<DankInventorySlotWrapper> storages = new ArrayList<>();

        for (int i = 0 ;i < slots;i++) {
            DankInventorySlotWrapper storage = new DankInventorySlotWrapper(dankInventoryFabric,i);
            storages.add(storage);
        }

        return new CombinedStorage<>(storages);
    }

}