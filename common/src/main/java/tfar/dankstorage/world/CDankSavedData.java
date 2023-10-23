package tfar.dankstorage.world;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import tfar.dankstorage.inventory.DankInterface;
import tfar.dankstorage.platform.Services;
import tfar.dankstorage.utils.DankStats;

import java.io.File;

public class CDankSavedData extends SavedData {

    protected final ServerLevel level;
    CompoundTag storage = new CompoundTag();
    DankInterface dankInventory;

    public CDankSavedData(ServerLevel level) {
        this.level = level;
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag) {
        compoundTag.put("contents", storage);
        return compoundTag;
    }

    public DankInterface createInventory(int frequency) {
        if (dankInventory == null) {
            dankInventory = Services.PLATFORM.createInventory(DankStats.zero,frequency);
            dankInventory.read(storage);
            dankInventory.setServer(level.getServer());
        }
        return dankInventory;
    }

    public DankInterface createFreshInventory(DankStats defaults, int frequency) {
        if (dankInventory == null) {
            dankInventory = Services.PLATFORM.createInventory(defaults, frequency);
            dankInventory.setServer(level.getServer());
        }
        return dankInventory;
    }

    public void setStats(DankStats stats, int frequency) {
        DankInterface dankInventory = createInventory(frequency);
        dankInventory.setDankStats(stats);
        write(dankInventory.save());
    }


    public void write(CompoundTag tag) {
        storage = tag;
        setDirty();
    }

    protected void load(CompoundTag compoundTag) {
        storage = compoundTag.getCompound("contents");
    }

    @Override
    public void save(File file) {
        super.save(file);
        //DankStorageForge.LOGGER.debug("Saving Dank Contents");
    }

    public static CDankSavedData loadStatic(CompoundTag compoundTag,ServerLevel level) {
        CDankSavedData dankSavedData = new CDankSavedData(level);
        dankSavedData.load(compoundTag);
        return dankSavedData;
    }

    public boolean clear() {
        storage = new CompoundTag();
        return true;
    }
}
