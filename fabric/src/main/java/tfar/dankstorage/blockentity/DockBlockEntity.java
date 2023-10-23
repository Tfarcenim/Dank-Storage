
package tfar.dankstorage.blockentity;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import tfar.dankstorage.DankStorageFabric;
import tfar.dankstorage.inventory.api.DankInventorySlotWrapper;
import tfar.dankstorage.world.DankInventoryFabric;

import java.util.ArrayList;
import java.util.List;

public class DockBlockEntity extends CommonDockBlockEntity<DankInventoryFabric> {

    public DockBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(DankStorageFabric.dank_tile, blockPos, blockState);
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