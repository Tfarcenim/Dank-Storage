package tfar.dankstorage.recipe;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import tfar.dankstorage.init.ModRecipeSerializers;

import javax.annotation.Nonnull;

public class UpgradeRecipe extends ShapedRecipe {

    public UpgradeRecipe(ShapedRecipe recipe) {
        super(recipe.getId(), "dank",recipe.category(), recipe.getWidth(), recipe.getHeight(), recipe.getIngredients(), recipe.getResultItem(null));
    }

    @Nonnull
    @Override
    public ItemStack assemble(CraftingContainer inv, RegistryAccess access) {
        ItemStack newBag = super.assemble(inv,access).copy();
        ItemStack oldBag = inv.getItem(4);
        //can't upgrade the backing inventory because there isn't one yet
        if (!oldBag.hasTag()) return newBag;
        newBag.setTag(oldBag.getTag());
        return newBag;
    }

    @Override
    public boolean matches(CraftingContainer $$0, Level $$1) {
        return super.matches($$0, $$1);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.upgrade;
    }
}
