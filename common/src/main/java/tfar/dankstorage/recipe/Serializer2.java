package tfar.dankstorage.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.ShapedRecipe;

public class Serializer2 extends ShapedRecipe.Serializer {

    public static final MapCodec<UpgradeRecipe> CODEC = RecordCodecBuilder.mapCodec(
                            instance -> instance.group(ShapedRecipe.Serializer.CODEC.forGetter(upgradeRecipe -> upgradeRecipe))
                                    .apply(instance,UpgradeRecipe::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, UpgradeRecipe> STREAM_CODEC = StreamCodec.of(
            ShapedRecipe.Serializer.STREAM_CODEC::encode, pBuffer -> new UpgradeRecipe(ShapedRecipe.Serializer.STREAM_CODEC.decode(pBuffer)));


    @Override
    public MapCodec<ShapedRecipe> codec() {
        return (MapCodec<ShapedRecipe>)(Object) CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, ShapedRecipe> streamCodec() {
        return  (StreamCodec<RegistryFriendlyByteBuf, ShapedRecipe>) (Object) STREAM_CODEC;
    }
}