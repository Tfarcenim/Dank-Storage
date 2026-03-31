package tfar.dankstorage.world;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import org.slf4j.Logger;
import tfar.dankstorage.inventory.DankInventory;
import tfar.dankstorage.platform.Services;
import tfar.dankstorage.utils.DankStats;

public class DankSavedData {

    DankInventory cache;
    DankStats stats;
    CompoundTag tag;
    int frequency;
    private static final Logger LOGGER = LogUtils.getLogger();

    private boolean dirty;

    public static final MapCodec<DankSavedData> MAP_CODEC = RecordCodecBuilder.mapCodec(dankSavedDataInstance -> dankSavedDataInstance.group(
            DankStats.CODEC.fieldOf("Stats").forGetter(d -> d.stats),
            CompoundTag.CODEC.fieldOf("contents").forGetter(d -> d.tag)
    ).apply(dankSavedDataInstance, DankSavedData::fromCodec));



    public DankSavedData(DankStats stats, CompoundTag tag) {
        this.stats = stats;
        this.tag = tag;
    }

    transient HolderLookup.Provider provider;

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public static DankSavedData fromCodec(DankStats stats, CompoundTag tag) {
        return new DankSavedData(stats,tag);
    }

    public DankInventory getOrCreateInventory(HolderLookup.Provider provider) {
        this.provider = provider;
        if (cache == null) {
            cache = Services.PLATFORM.createInventory(stats,this);

            try (ProblemReporter.ScopedCollector reporter = new ProblemReporter.ScopedCollector(problemPath(), LOGGER)) {
                ValueInput input = TagValueInput.create(reporter, provider, tag);
                cache.load(input, tag);
            }

        }
        if (cache.items.size() != stats.slots) {
            cache.setTo(stats);
        }
        return cache;
    }

    public void setStats(DankStats stats) {
        this.stats = stats;
    }

    public DankStats getStats() {
        return stats;
    }

    public boolean clear(HolderLookup.Provider provider) {
        DankInventory dankInventory = getOrCreateInventory(provider);
        return true;
    }

    public void setDirty() {
        setDirty(true);
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
        if (dirty) {
            save(provider);
        }
    }

    public boolean isDirty() {
        return dirty;
    }

    public void save(HolderLookup.Provider provider) {
        if (cache != null) {
            try (ProblemReporter.ScopedCollector reporter = new ProblemReporter.ScopedCollector(this.problemPath(), LOGGER)) {
                TagValueOutput output = TagValueOutput.createWithContext(reporter, provider);
                tag = cache.save(output);
            }
        }
    }

    public ProblemReporter.PathElement problemPath() {
        return new DankPathElement(this);
    }

    private record DankPathElement(DankSavedData savedData) implements ProblemReporter.PathElement {
        @Override
        public String get() {
            return "Dank @"+savedData.frequency;
        }
    }
}
