package tfar.dankstorage.init;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import tfar.dankstorage.DankStorage;
import tfar.dankstorage.recipe.Serializer2;
import tfar.dankstorage.recipe.UpgradeRecipe;

public class ModRecipeSerializers {

    public static RecipeSerializer<UpgradeRecipe> upgrade = new RecipeSerializer<>(Serializer2.CODEC,Serializer2.STREAM_CODEC);

    static {
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, DankStorage.id("upgrade"),upgrade);
    }

    public static void init() {

    }
}
