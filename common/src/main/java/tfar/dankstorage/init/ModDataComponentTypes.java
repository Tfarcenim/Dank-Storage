package tfar.dankstorage.init;

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.Unit;
import net.minecraft.world.item.ItemStack;
import tfar.dankstorage.utils.SerializationHelper;
import tfar.dankstorage.utils.PickupMode;
import tfar.dankstorage.utils.UseType;

public class ModDataComponentTypes {

    public static final Codec<PickupMode> PICKUP_MODE_CODEC = StringRepresentable.fromEnum(PickupMode::values);
    public static final Codec<UseType> USE_TYPE_CODEC = StringRepresentable.fromEnum(UseType::values);

    public static final DataComponentType<Integer> FREQUENCY = DataComponentType.<Integer>builder()
            .persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.INT).build();

    public static final DataComponentType<PickupMode> PICKUP_MODE = DataComponentType.<PickupMode>builder()
            .persistent(PICKUP_MODE_CODEC).networkSynchronized(SerializationHelper.enumCodec(PickupMode.class)).build();

    public static final DataComponentType<UseType> USE_TYPE = DataComponentType.<UseType>builder()
            .persistent(USE_TYPE_CODEC).networkSynchronized(SerializationHelper.enumCodec(UseType.class)).build();

    public static final DataComponentType<ItemStack> SELECTED = DataComponentType.<ItemStack>builder()
            .persistent(SerializationHelper.LARGE_CODEC).networkSynchronized(SerializationHelper.LARGE_OPTIONAL_STREAM_CODEC).build();

    public static final DataComponentType<Unit> OREDICT = DataComponentType.<Unit>builder()
            .persistent(Unit.CODEC).networkSynchronized(StreamCodec.unit(Unit.INSTANCE)).build();

}
