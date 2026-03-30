package tfar.dankstorage.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import org.jetbrains.annotations.Nullable;
import tfar.dankstorage.DankStorage;
import tfar.dankstorage.utils.DankStats;

import java.util.List;

public class DankSavedDatas extends SavedData {

    private static final Identifier DATAS_FILE_ID = DankStorage.id("datas");

    Int2ObjectMap<DankSavedData> datas = new Int2ObjectOpenHashMap<>();
    private int nextId=1;


    public static final Codec<DankSavedDatas> CODEC = RecordCodecBuilder.create(
            i -> i.group(
                            WithId.CODEC
                                    .listOf()
                                    .optionalFieldOf("datas", List.of())
                                    .forGetter(d -> d.datas.int2ObjectEntrySet().stream().map(WithId::from).toList()),
                            Codec.INT.fieldOf("next_id").forGetter(r -> r.nextId)
                    )
                    .apply(i, DankSavedDatas::new)
    );

    public static final SavedDataType<DankSavedDatas> TYPE = new SavedDataType<>(DATAS_FILE_ID,DankSavedDatas::new, CODEC, null);

    public DankSavedDatas() {
        setDirty();
    }

    public DankSavedDatas(List<WithId> datas, int nextId) {
        for (WithId data : datas) {
            this.datas.put(data.id, data.data);
            data.data.setFrequency(data.id);
        }
        this.nextId = nextId;
    }


    public @Nullable DankSavedData get(int id) {
        return datas.get(id);
    }

    public DankSavedData assignNextFreeId(MinecraftServer server, DankStats startingStats) {
        int frequency = getUniqueId();
        DankSavedData data = new DankSavedData(startingStats,new CompoundTag());
        data.setFrequency(frequency);
        this.datas.put(frequency,data);
        return data;
    }

    public static DankSavedDatas getOrCreate(MinecraftServer server) {
        return server.getDataStorage().computeIfAbsent(TYPE);
    }

    @Nullable
    public static DankSavedDatas get(MinecraftServer server) {
        return server.getDataStorage().get(TYPE);
    }

    private int getUniqueId() {
        return ++this.nextId;
    }

    public int getNextId() {
        return nextId;
    }

    public record WithId(int id, DankSavedData data) {
        public static final Codec<WithId> CODEC = RecordCodecBuilder.create(
                i -> i.group(Codec.INT.fieldOf("id").forGetter(WithId::id), DankSavedData.MAP_CODEC.forGetter(WithId::data))
                        .apply(i, WithId::new)
        );

        public static WithId from(Int2ObjectMap.Entry<DankSavedData> entry) {
            return new WithId(entry.getIntKey(), entry.getValue());
        }
    }

    @Override
    public void setDirty() {
        super.setDirty();
    }
}
