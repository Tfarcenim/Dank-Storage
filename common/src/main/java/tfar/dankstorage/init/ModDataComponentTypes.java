package tfar.dankstorage.init;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.Unit;
import tfar.dankstorage.DankStorage;
import tfar.dankstorage.utils.ItemStackComponent;
import tfar.dankstorage.utils.SerializationHelper;
import tfar.dankstorage.utils.PickupMode;
import tfar.dankstorage.utils.UseType;

public class ModDataComponentTypes {

    public static final Codec<PickupMode> PICKUP_MODE_CODEC = StringRepresentable.fromEnum(PickupMode::values);
    public static final Codec<UseType> USE_TYPE_CODEC = StringRepresentable.fromEnum(UseType::values);

    public static final DataComponentType<Integer> FREQUENCY = DataComponentType.<Integer>builder()
            .persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.INT).build();

    public static final DataComponentType<PickupMode> PICKUP_MODE = DataComponentType.<PickupMode>builder()
            .persistent(PICKUP_MODE_CODEC).networkSynchronized(SerializationHelper.enumStreamCodec(PickupMode.class)).build();

    public static final DataComponentType<UseType> USE_TYPE = DataComponentType.<UseType>builder()
            .persistent(USE_TYPE_CODEC).networkSynchronized(SerializationHelper.enumStreamCodec(UseType.class)).build();

    public static final DataComponentType<ItemStackComponent> SELECTED = DataComponentType.<ItemStackComponent>builder()
            .persistent(ItemStackComponent.CODEC).networkSynchronized(ItemStackComponent.STREAM_CODEC).build();

    public static final DataComponentType<Unit> OREDICT = DataComponentType.<Unit>builder()
            .persistent(Unit.CODEC).networkSynchronized(StreamCodec.unit(Unit.INSTANCE)).build();

    static {
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, DankStorage.id("frequency"),FREQUENCY);
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, DankStorage.id("pickup_mode"),PICKUP_MODE);
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, DankStorage.id("use_type"),USE_TYPE);
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, DankStorage.id("selected"),SELECTED);
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, DankStorage.id("oredict"),OREDICT);
    }

    public static void init() {

    }

}
