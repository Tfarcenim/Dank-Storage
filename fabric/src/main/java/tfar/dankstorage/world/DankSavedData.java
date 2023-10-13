package tfar.dankstorage.world;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import tfar.dankstorage.utils.DankStats;

public class DankSavedData extends CDankSavedData {

    DankInventory dankInventory;
    public DankSavedData(ServerLevel level) {
        super(level);
    }

    public DankInventory createInventory(int frequency) {
        if (dankInventory == null) {
            dankInventory = new DankInventory(DankStats.zero, frequency);
            dankInventory.read(storage);
            dankInventory.server = level.getServer();
        }
        return dankInventory;
    }

    public DankInventory createFreshInventory(DankStats defaults,int frequency) {
        if (dankInventory == null) {
            dankInventory = new DankInventory(defaults, frequency);
            dankInventory.server = level.getServer();
        }
        return dankInventory;
    }

    public void setStats(DankStats stats, int frequency) {
        DankInventory dankInventory = createInventory(frequency);
        dankInventory.setDankStats(stats);
        write(dankInventory.save());
    }

    public static DankSavedData loadStatic(CompoundTag compoundTag,ServerLevel level) {
        DankSavedData dankSavedData = new DankSavedData(level);
        dankSavedData.load(compoundTag);
        return dankSavedData;
    }
}
