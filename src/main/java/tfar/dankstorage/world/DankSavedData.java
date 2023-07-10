package tfar.dankstorage.world;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;
import tfar.dankstorage.utils.DankStats;

import java.io.File;

public class DankSavedData extends SavedData {

    CompoundTag storage = new CompoundTag();
    public DankSavedData() {
    }

    public DankInventory createInventory(int frequency) {
        DankInventory dankInventory = new DankInventory(DankStats.zero, frequency);
        dankInventory.read(storage);
        return dankInventory;
    }

    public DankInventory createFreshInventory(DankStats defaults,int frequency) {
        DankInventory dankInventory = new DankInventory(defaults, frequency);
        write(dankInventory.save());
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

    public static DankSavedData loadStatic(CompoundTag compoundTag) {
        DankSavedData dankSavedData = new DankSavedData();
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
