package tfar.dankstorage.recipe;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import tfar.dankstorage.DankStorageFabric;

import javax.annotation.Nonnull;

public class UpgradeRecipe extends ShapedRecipe {

    public UpgradeRecipe(ShapedRecipe recipe) {
        super(recipe.getId(), "dank",recipe.category(), recipe.getWidth(), recipe.getHeight(), recipe.getIngredients(), recipe.getResultItem(null));
    }

    @Nonnull
    @Override
    public ItemStack assemble(CraftingContainer inv, RegistryAccess registryAccess) {
        ItemStack newBag = super.assemble(inv,registryAccess).copy();
        ItemStack oldBag = inv.getItem(4);
        //can't upgrade the backing inventory because there isn't one yet
        if (!oldBag.hasTag()) return newBag;
        newBag.setTag(oldBag.getTag());
        return newBag;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return DankStorageFabric.upgrade;
    }
}
