package tfar.dankstorage.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import tfar.dankstorage.init.ModDataComponentTypes;
import tfar.dankstorage.init.ModRecipeSerializers;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class UpgradeRecipe extends ShapedRecipe {

    public UpgradeRecipe(ShapedRecipe recipe) {
        super("dank",recipe.category(), recipe.pattern, recipe.getResultItem(null));
    }

    protected static final List<DataComponentType> types = new ArrayList<>();
    static {
        types.add(ModDataComponentTypes.FREQUENCY);
        types.add(ModDataComponentTypes.PICKUP_MODE);
        types.add(ModDataComponentTypes.USE_TYPE);
        types.add(ModDataComponentTypes.SELECTED);
        types.add(ModDataComponentTypes.OREDICT);
    }

    @Nonnull
    @Override
    public ItemStack assemble(CraftingInput inv, HolderLookup.Provider access) {
        ItemStack newBag = super.assemble(inv,access).copy();
        ItemStack oldBag = inv.getItem(4);
        //can't upgrade the backing inventory because there isn't one yet
        if (oldBag.getComponents().isEmpty()) return newBag;

        for (DataComponentType type : types) {
            if (oldBag.has(type)) {
                newBag.set(type,oldBag.get(type));
            }
        }

        return newBag;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.upgrade;
    }
}
