package tfar.dankstorage.world;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import tfar.dankstorage.utils.DankStats;

public class DankSavedData extends CDankSavedData {

    DankInventoryFabric dankInventoryFabric;
    public DankSavedData(ServerLevel level) {
        super(level);
    }

    public DankInventoryFabric createInventory(int frequency) {
        if (dankInventoryFabric == null) {
            dankInventoryFabric = new DankInventoryFabric(DankStats.zero, frequency);
            dankInventoryFabric.read(storage);
            dankInventoryFabric.server = level.getServer();
        }
        return dankInventoryFabric;
    }

    public DankInventoryFabric createFreshInventory(DankStats defaults, int frequency) {
        if (dankInventoryFabric == null) {
            dankInventoryFabric = new DankInventoryFabric(defaults, frequency);
            dankInventoryFabric.server = level.getServer();
        }
        return dankInventoryFabric;
    }

    public void setStats(DankStats stats, int frequency) {
        DankInventoryFabric dankInventoryFabric = createInventory(frequency);
        dankInventoryFabric.setDankStats(stats);
        write(dankInventoryFabric.save());
    }

    public static DankSavedData loadStatic(CompoundTag compoundTag,ServerLevel level) {
        DankSavedData dankSavedData = new DankSavedData(level);
        dankSavedData.load(compoundTag);
        return dankSavedData;
    }
}
