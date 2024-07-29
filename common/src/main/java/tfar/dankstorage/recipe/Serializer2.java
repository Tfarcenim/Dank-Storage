package tfar.dankstorage.recipe;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;

import javax.annotation.Nonnull;

public class Serializer2 extends ShapedRecipe.Serializer {

    public static final MapCodec<UpgradeRecipe> CODEC = RecordCodecBuilder.mapCodec(
            p_340778_ -> p_340778_.group(
                            Codec.STRING.optionalFieldOf("group", "").forGetter(p_311729_ -> p_311729_.group),
                            CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(p_311732_ -> p_311732_.category),
                            ShapedRecipePattern.MAP_CODEC.forGetter(p_311733_ -> p_311733_.pattern),
                            ItemStack.STRICT_CODEC.fieldOf("result").forGetter(p_311730_ -> p_311730_.result),
                            Codec.BOOL.optionalFieldOf("show_notification", Boolean.valueOf(true)).forGetter(p_311731_ -> p_311731_.showNotification)
                    )
                    .apply(p_340778_, ShapedRecipe::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, UpgradeRecipe> STREAM_CODEC = StreamCodec.of(
            ShapedRecipe.Serializer::toNetwork, ShapedRecipe.Serializer::fromNetwork
    );


    @Override
    public UpgradeRecipe fromJson(ResourceLocation location, JsonObject json) {
        return new UpgradeRecipe(super.fromJson(location, json));
    }


    @Override
    public UpgradeRecipe fromNetwork(@Nonnull ResourceLocation p_199426_1_, FriendlyByteBuf p_199426_2_) {
        return new UpgradeRecipe(super.fromNetwork(p_199426_1_, p_199426_2_));
    }
}