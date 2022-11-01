package tfar.dankstorage.recipe;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.ShapedRecipe;

import javax.annotation.Nonnull;

public class Serializer2 extends ShapedRecipe.Serializer {
    @Override
    public UpgradeRecipe fromJson(ResourceLocation location, JsonObject json) {
        return new UpgradeRecipe(super.fromJson(location, json));
    }


    @Override
    public UpgradeRecipe fromNetwork(@Nonnull ResourceLocation p_199426_1_, FriendlyByteBuf p_199426_2_) {
        return new UpgradeRecipe(super.fromNetwork(p_199426_1_, p_199426_2_));
    }
}