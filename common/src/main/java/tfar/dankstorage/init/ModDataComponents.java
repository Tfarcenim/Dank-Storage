package tfar.dankstorage.init;

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import tfar.dankstorage.utils.SerializationHelper;
import tfar.dankstorage.utils.PickupMode;
import tfar.dankstorage.utils.UseType;

public class ModDataComponents {

    public static final Codec<PickupMode> PICKUP_MODE_CODEC = StringRepresentable.fromEnum(PickupMode::values);
    public static final Codec<UseType> USE_TYPE_CODEC = StringRepresentable.fromEnum(UseType::values);

    public static final DataComponentType<Integer> FREQUENCY = DataComponentType.<Integer>builder()
            .persistent(ExtraCodecs.intRange(0,Integer.MAX_VALUE)).networkSynchronized(ByteBufCodecs.INT).build();

    public static final DataComponentType<PickupMode> PICKUP_MODE = DataComponentType.<PickupMode>builder()
            .persistent(PICKUP_MODE_CODEC).networkSynchronized(SerializationHelper.enumCodec(PickupMode.class)).build();

    public static final DataComponentType<UseType> USE_TYPE = DataComponentType.<UseType>builder()
            .persistent(USE_TYPE_CODEC).networkSynchronized(SerializationHelper.enumCodec(UseType.class)).build();

    public static final DataComponentType<Integer> SELECTED = DataComponentType.<Integer>builder()
            .persistent(ExtraCodecs.intRange(0,99)).networkSynchronized(ByteBufCodecs.INT).build();

    public static final DataComponentType<Boolean> OREDICT = DataComponentType.<Boolean>builder()
            .persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build();

}
