package tfar.dankstorage.utils;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CommonUtils {
    private static final DecimalFormat decimalFormat = new DecimalFormat("0.##");

    public static String formatLargeNumber(int number) {

        if (number >= 1000000000) return decimalFormat.format(number / 1000000000f) + "b";
        if (number >= 1000000) return decimalFormat.format(number / 1000000f) + "m";
        if (number >= 1000) return decimalFormat.format(number / 1000f) + "k";

        return Float.toString(number).replaceAll("\\.?0*$", "");
    }

    private static List<CraftingRecipe> REVERSIBLE3x3 = new ArrayList<>();
    private static List<CraftingRecipe> REVERSIBLE2x2 = new ArrayList<>();
    private static boolean cached = false;

    public static void uncacheRecipes(RecipeManager manager) {
        cached = false;
    }

    public static Pair<ItemStack,Integer> compress(ItemStack stack, RegistryAccess registryAccess) {

        for (CraftingRecipe recipe : REVERSIBLE3x3) {
            if (recipe.getIngredients().get(0).test(stack)) {
                return Pair.of(recipe.getResultItem(registryAccess),9);
            }
        }

        for (CraftingRecipe recipe : REVERSIBLE2x2) {
            if (recipe.getIngredients().get(0).test(stack)) {
                return Pair.of(recipe.getResultItem(registryAccess),4);
            }
        }
        return Pair.of(ItemStack.EMPTY,0);
    }

    public static boolean canCompress(ServerLevel level, ItemStack stack) {
        if (!cached) {
            REVERSIBLE3x3 = findReversibles(level,3);
            REVERSIBLE2x2 = findReversibles(level,2);
            cached = true;
        }

        for (CraftingRecipe recipe : REVERSIBLE3x3) {
            if (recipe.getIngredients().get(0).test(stack)) {
                return stack.getCount() >=9;
            }
        }

        for (CraftingRecipe recipe : REVERSIBLE2x2) {
            if (recipe.getIngredients().get(0).test(stack)) {
                return stack.getCount()>=4;
            }
        }

        return false;
    }
    public static List<CraftingRecipe> findReversibles(ServerLevel level,int size) {
        List<CraftingRecipe> compactingRecipes = new ArrayList<>();
        List<CraftingRecipe> recipes = level.getRecipeManager().getAllRecipesFor(RecipeType.CRAFTING);

        for (CraftingRecipe recipe : recipes) {
            if (recipe instanceof ShapedRecipe shapedRecipe) {
                int x = shapedRecipe.getWidth();
                int y = shapedRecipe.getHeight();
                if (x == size && x == y) {

                    List<Ingredient> inputs = shapedRecipe.getIngredients();

                    Ingredient first = inputs.get(0);
                    if (first != Ingredient.EMPTY) {
                        boolean same = true;
                        for (int i = 1; i < x * y;i++) {
                            Ingredient next = inputs.get(i);
                            if (next != first) {
                                same = false;
                                break;
                            }
                        }
                        if (same && shapedRecipe.getResultItem(level.registryAccess()).getCount() == 1) {
                            DUMMY.setItem(0,shapedRecipe.getResultItem(level.registryAccess()));

                            level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, DUMMY, level).ifPresent(reverseRecipe -> {
                                if (reverseRecipe.getResultItem(level.registryAccess()).getCount() == size * size) {
                                    compactingRecipes.add(shapedRecipe);
                                }
                            });
                        }
                    }
                }
            }
        }
        return compactingRecipes;
    }
    @SuppressWarnings("ConstantConditions")
    private static final CraftingContainer DUMMY = new TransientCraftingContainer(null,1,1) {
        @Override
        public void setItem(int i, ItemStack itemStack) {
            getItems().set(i, itemStack);
        }

        @Override
        public ItemStack removeItem(int i, int j) {
            return ContainerHelper.removeItem(getItems(), i, j);
        }
    };


}
