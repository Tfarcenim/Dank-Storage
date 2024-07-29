package tfar.dankstorage.world;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;

public class MaxId extends SavedData {
    private int maxId;
    @Override
    public CompoundTag save(CompoundTag pCompoundTag,HolderLookup.Provider provider) {
        pCompoundTag.putInt("max_id",maxId);
        return pCompoundTag;
    }

    public static MaxId loadStatic(CompoundTag compoundTag) {
        MaxId id = new MaxId();
        id.load(compoundTag);
        return id;
    }

    public int getMaxId() {
        return maxId;
    }

    public void increment() {
        maxId++;
        setDirty();
    }

    public void setMaxId(int maxId) {
        this.maxId = maxId;
        setDirty();
    }

    protected void load(CompoundTag compoundTag) {
        maxId = compoundTag.getInt("max_id");
    }
}
