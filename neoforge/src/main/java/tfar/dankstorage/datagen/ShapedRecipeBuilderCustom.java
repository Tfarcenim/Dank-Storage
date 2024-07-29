package tfar.dankstorage.datagen;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.level.ItemLike;
import tfar.dankstorage.recipe.Serializer2;

public class ShapedRecipeBuilderCustom implements RecipeBuilder {
    private final RecipeCategory category;
    private final Item result;
    private final int count;
    private final ItemStack resultStack; // Neo: add stack result support
    private final List<String> rows = Lists.newArrayList();
    private final Map<Character, Ingredient> key = Maps.newLinkedHashMap();
    private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();
    @Nullable
    private String group;
    private boolean showNotification = true;
    private RecipeSerializer<?> serializer;

    public ShapedRecipeBuilderCustom(RecipeCategory pCategory, ItemLike pResult, int pCount) {
        this(pCategory, new ItemStack(pResult, pCount));
    }

    public ShapedRecipeBuilderCustom(RecipeCategory p_249996_, ItemStack result) {
        this.category = p_249996_;
        this.result = result.getItem();
        this.count = result.getCount();
        this.resultStack = result;
    }

    /**
     * Creates a new builder for a shaped recipe.
     */
    public static ShapedRecipeBuilderCustom shaped(RecipeCategory pCategory, ItemLike pResult) {
        return shaped(pCategory, pResult, 1);
    }

    /**
     * Creates a new builder for a shaped recipe.
     */
    public static ShapedRecipeBuilderCustom shaped(RecipeCategory pCategory, ItemLike pResult, int pCount) {
        return new ShapedRecipeBuilderCustom(pCategory, pResult, pCount);
    }

    public static ShapedRecipeBuilderCustom shaped(RecipeCategory p_251325_, ItemStack result) {
        return new ShapedRecipeBuilderCustom(p_251325_, result);
    }

    /**
     * Adds a key to the recipe pattern.
     */
    public ShapedRecipeBuilderCustom define(Character pSymbol, TagKey<Item> pTag) {
        return this.define(pSymbol, Ingredient.of(pTag));
    }

    /**
     * Adds a key to the recipe pattern.
     */
    public ShapedRecipeBuilderCustom define(Character pSymbol, ItemLike pItem) {
        return this.define(pSymbol, Ingredient.of(pItem));
    }

    public RecipeBuilder serializer(RecipeSerializer<?> serializer) {
        this.serializer = serializer;
        return this;
    }

    /**
     * Adds a key to the recipe pattern.
     */
    public ShapedRecipeBuilderCustom define(Character pSymbol, Ingredient pIngredient) {
        if (this.key.containsKey(pSymbol)) {
            throw new IllegalArgumentException("Symbol '" + pSymbol + "' is already defined!");
        } else if (pSymbol == ' ') {
            throw new IllegalArgumentException("Symbol ' ' (whitespace) is reserved and cannot be defined");
        } else {
            this.key.put(pSymbol, pIngredient);
            return this;
        }
    }

    /**
     * Adds a new entry to the patterns for this recipe.
     */
    public ShapedRecipeBuilderCustom pattern(String pPattern) {
        if (!this.rows.isEmpty() && pPattern.length() != this.rows.get(0).length()) {
            throw new IllegalArgumentException("Pattern must be the same width on every line!");
        } else {
            this.rows.add(pPattern);
            return this;
        }
    }

    @Override
    public ShapedRecipeBuilderCustom unlockedBy(String pName, Criterion<?> pCriterion) {
        this.criteria.put(pName, pCriterion);
        return this;
    }

    @Override
    public ShapedRecipeBuilderCustom group(@Nullable String pGroupName) {
        this.group = pGroupName;
        return this;
    }

    public ShapedRecipeBuilderCustom showNotification(boolean pShowNotification) {
        this.showNotification = pShowNotification;
        return this;
    }

    @Override
    public Item getResult() {
        return this.result;
    }

    @Override
    public void save(RecipeOutput pRecipeOutput, ResourceLocation pId) {
        ShapedRecipePattern shapedrecipepattern = this.ensureValid(pId);
        Advancement.Builder advancement$builder = pRecipeOutput.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(pId))
                .rewards(AdvancementRewards.Builder.recipe(pId))
                .requirements(AdvancementRequirements.Strategy.OR);
        this.criteria.forEach(advancement$builder::addCriterion);
        ShapedRecipe shapedrecipe = new ShapedRecipe(
                Objects.requireNonNullElse(this.group, ""),
                RecipeBuilder.determineBookCategory(this.category),
                shapedrecipepattern,
                this.resultStack,
                this.showNotification
        );
        pRecipeOutput.accept(pId, shapedrecipe, advancement$builder.build(pId.withPrefix("recipes/" + this.category.getFolderName() + "/")));
    }

    private ShapedRecipePattern ensureValid(ResourceLocation pLoaction) {
        if (this.criteria.isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + pLoaction);
        } else {
            return ShapedRecipePattern.of(this.key, this.rows);
        }
    }


}
