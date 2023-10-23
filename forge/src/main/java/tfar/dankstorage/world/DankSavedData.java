package tfar.dankstorage.world;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import tfar.dankstorage.utils.DankStats;

public class DankSavedData extends CDankSavedData {
    DankInventoryForge dankInventoryForge;
    public DankSavedData(ServerLevel level) {
        super(level);
    }

    public DankInventoryForge createInventory(int frequency) {
        if (dankInventoryForge == null) {
            dankInventoryForge = new DankInventoryForge(DankStats.zero, frequency);
            dankInventoryForge.read(storage);
            dankInventoryForge.server = level.getServer();
        }
        return dankInventoryForge;
    }

    public DankInventoryForge createFreshInventory(DankStats defaults, int frequency) {
        if (dankInventoryForge == null) {
            dankInventoryForge = new DankInventoryForge(defaults, frequency);
            dankInventoryForge.server = level.getServer();
        }
        return dankInventoryForge;
    }

    public void setStats(DankStats stats, int frequency) {
        DankInventoryForge dankInventoryForge = createInventory(frequency);
        dankInventoryForge.setDankStats(stats);
        write(dankInventoryForge.save());
    }

    public static DankSavedData loadStatic(CompoundTag compoundTag,ServerLevel level) {
        DankSavedData dankSavedData = new DankSavedData(level);
        dankSavedData.load(compoundTag);
        return dankSavedData;
    }
}
