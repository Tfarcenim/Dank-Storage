package tfar.dankstorage.world;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.io.File;

public abstract class CDankSavedData extends SavedData {

    protected final ServerLevel level;
    CompoundTag storage = new CompoundTag();

    protected CDankSavedData(ServerLevel level) {
        this.level = level;
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

    protected void load(CompoundTag compoundTag) {
        storage = compoundTag.getCompound("contents");
    }

    @Override
    public void save(File file) {
        super.save(file);
        //DankStorageForge.LOGGER.debug("Saving Dank Contents");
    }

    public boolean clear() {
        storage = new CompoundTag();
        return true;
    }
}
