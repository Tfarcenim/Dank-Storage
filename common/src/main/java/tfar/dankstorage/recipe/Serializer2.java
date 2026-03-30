package tfar.dankstorage.recipe;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.ShapedRecipe;

public class Serializer2 {

    public static final MapCodec<UpgradeRecipe> CODEC = ShapedRecipe.MAP_CODEC.xmap(UpgradeRecipe::new, upgradeRecipe -> upgradeRecipe);
    public static final StreamCodec<RegistryFriendlyByteBuf, UpgradeRecipe> STREAM_CODEC = StreamCodec.of(
            ShapedRecipe.STREAM_CODEC::encode, pBuffer -> new UpgradeRecipe(ShapedRecipe.STREAM_CODEC.decode(pBuffer)));

}