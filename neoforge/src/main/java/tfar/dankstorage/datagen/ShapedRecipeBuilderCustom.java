package tfar.dankstorage.datagen;

import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.level.ItemLike;
import tfar.dankstorage.recipe.UpgradeRecipe;

public class ShapedRecipeBuilderCustom extends ShapedRecipeBuilder {

    public ShapedRecipeBuilderCustom(HolderGetter<Item> items, RecipeCategory category, ItemStackTemplate result) {
        super(items, category, result);
    }


    /**
     * Creates a new builder for a shaped recipe.
     */
    public static ShapedRecipeBuilderCustom shapedC(HolderGetter<Item> items,RecipeCategory pCategory, ItemLike pResult) {
        return shapedC(items,pCategory, pResult, 1);
    }

    /**
     * Creates a new builder for a shaped recipe.
     */
    public static ShapedRecipeBuilderCustom shapedC(HolderGetter<Item> items,RecipeCategory pCategory, ItemLike pResult, int pCount) {
        return new ShapedRecipeBuilderCustom(items,pCategory, new ItemStackTemplate(pResult.asItem(), pCount));
    }

    /**
     * Adds a key to the recipe pattern.
     */

    public RecipeBuilder serializer() {
        return this;
    }

    @Override
    public void save(RecipeOutput output, ResourceKey id) {
        ShapedRecipePattern pattern = ShapedRecipePattern.of(this.key, this.rows);
        UpgradeRecipe recipe = new UpgradeRecipe(new ShapedRecipe(
                RecipeBuilder.createCraftingCommonInfo(this.showNotification),
                RecipeBuilder.createCraftingBookInfo(this.category, this.group),
                pattern,
                this.result
        ));
        output.accept(id, recipe, this.advancementBuilder.build(output, id, this.category));
    }

}
