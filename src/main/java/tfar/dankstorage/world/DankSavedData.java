package tfar.dankstorage.world;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import tfar.dankstorage.utils.DankStats;

import java.io.File;

public class DankSavedData extends SavedData {

    private final ServerLevel level;

    CompoundTag storage = new CompoundTag();

    DankInventory dankInventory;
    public DankSavedData(ServerLevel level) {
        this.level = level;
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

    @Override
    public CompoundTag save(CompoundTag compoundTag) {
        compoundTag.put("contents", storage);
        return compoundTag;
    }

    public void write(CompoundTag tag) {
        storage = tag;
        setDirty();
    }

    public void setStats(DankStats stats,int frequency) {
        DankInventory dankInventory = createInventory(frequency);
        dankInventory.setDankStats(stats);
        write(dankInventory.save());
    }

    public static DankSavedData loadStatic(CompoundTag compoundTag,ServerLevel level) {
        DankSavedData dankSavedData = new DankSavedData(level);
        dankSavedData.load(compoundTag);
        return dankSavedData;
    }

    protected void load(CompoundTag compoundTag) {
        storage = compoundTag.getCompound("contents");
    }

    @Override
    public void save(File file) {
        super.save(file);
        //DankStorage.LOGGER.debug("Saving Dank Contents");
    }

    public boolean clear() {
        storage = new CompoundTag();
        return true;
    }
}
