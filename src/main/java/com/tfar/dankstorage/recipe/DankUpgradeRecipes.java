package com.tfar.dankstorage.recipe;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

import static com.tfar.dankstorage.DankStorage.Objects.*;
import static com.tfar.dankstorage.recipe.DankUpgradeRecipes.Tags.*;

public class DankUpgradeRecipes {

  public static class DankUpgradeRecipe1 extends AbstractDankUpgradeRecipe {

    public DankUpgradeRecipe1(ResourceLocation idIn) {
      super(idIn, NonNullList.from(Ingredient.EMPTY,
              Ingredient.fromTag(REDSTONE_BLOCK), Ingredient.fromTag(REDSTONE_BLOCK), Ingredient.fromTag(REDSTONE_BLOCK),
              Ingredient.fromTag(REDSTONE_BLOCK), Ingredient.fromItems(dank_1), Ingredient.fromTag(REDSTONE_BLOCK),
              Ingredient.fromTag(REDSTONE_BLOCK), Ingredient.fromTag(REDSTONE_BLOCK),Ingredient.fromTag(REDSTONE_BLOCK)), new ItemStack(dank_2));
    }

    @Nonnull
    @Override
    public IRecipeSerializer<?> getSerializer() {
      return upgrade1;
    }
  }

  public static class DankUpgradeRecipe2 extends AbstractDankUpgradeRecipe {

    public DankUpgradeRecipe2(ResourceLocation idIn) {
      super(idIn, NonNullList.from(Ingredient.EMPTY,
              Ingredient.fromTag(GOLD_BLOCK), Ingredient.fromTag(GOLD_BLOCK), Ingredient.fromTag(GOLD_BLOCK),
              Ingredient.fromTag(GOLD_BLOCK), Ingredient.fromItems(dank_2), Ingredient.fromTag(GOLD_BLOCK),
              Ingredient.fromTag(GOLD_BLOCK), Ingredient.fromTag(GOLD_BLOCK),Ingredient.fromTag(GOLD_BLOCK)), new ItemStack(dank_3));
    }

    @Nonnull
    @Override
    public IRecipeSerializer<?> getSerializer() {
      return upgrade2;
    }
  }

  public static class DankUpgradeRecipe3 extends AbstractDankUpgradeRecipe {

    public DankUpgradeRecipe3(ResourceLocation idIn) {
      super(idIn, NonNullList.from(Ingredient.EMPTY,
              Ingredient.fromTag(EMERALD_BLOCK), Ingredient.fromTag(EMERALD_BLOCK), Ingredient.fromTag(EMERALD_BLOCK),
              Ingredient.fromTag(EMERALD_BLOCK), Ingredient.fromItems(dank_3), Ingredient.fromTag(EMERALD_BLOCK),
              Ingredient.fromTag(EMERALD_BLOCK), Ingredient.fromTag(EMERALD_BLOCK),Ingredient.fromTag(EMERALD_BLOCK)), new ItemStack(dank_4));
    }

    @Nonnull
    @Override
    public IRecipeSerializer<?> getSerializer() {
      return upgrade3;
    }
  }

  public static class DankUpgradeRecipe4 extends AbstractDankUpgradeRecipe {

    public DankUpgradeRecipe4(ResourceLocation idIn) {
      super(idIn, NonNullList.from(Ingredient.EMPTY,
              Ingredient.fromTag(DIAMOND_BLOCK), Ingredient.fromTag(DIAMOND_BLOCK), Ingredient.fromTag(DIAMOND_BLOCK),
              Ingredient.fromTag(DIAMOND_BLOCK), Ingredient.fromItems(dank_4), Ingredient.fromTag(DIAMOND_BLOCK),
              Ingredient.fromTag(DIAMOND_BLOCK), Ingredient.fromTag(DIAMOND_BLOCK),Ingredient.fromTag(DIAMOND_BLOCK)), new ItemStack(dank_5));
    }

    @Nonnull
    @Override
    public IRecipeSerializer<?> getSerializer() {
      return upgrade4;
    }
  }

  public static class DankUpgradeRecipe5 extends AbstractDankUpgradeRecipe {

    public DankUpgradeRecipe5(ResourceLocation idIn) {
      super(idIn, NonNullList.from(Ingredient.EMPTY,
              Ingredient.fromItems(Items.OBSIDIAN), Ingredient.fromItems(Items.OBSIDIAN), Ingredient.fromItems(Items.OBSIDIAN),
              Ingredient.fromItems(Items.OBSIDIAN), Ingredient.fromItems(dank_5), Ingredient.fromItems(Items.OBSIDIAN),
              Ingredient.fromItems(Items.OBSIDIAN), Ingredient.fromItems(Items.OBSIDIAN),Ingredient.fromItems(Items.OBSIDIAN)), new ItemStack(dank_6));
    }

    @Nonnull
    @Override
    public IRecipeSerializer<?> getSerializer() {
      return upgrade5;
    }
  }

  public static class DankUpgradeRecipe6 extends AbstractDankUpgradeRecipe {

    public DankUpgradeRecipe6(ResourceLocation idIn) {
      super(idIn, NonNullList.from(Ingredient.EMPTY,
              Ingredient.fromItems(Items.NETHER_STAR), Ingredient.fromItems(Items.NETHER_STAR), Ingredient.fromItems(Items.NETHER_STAR),
              Ingredient.fromItems(Items.NETHER_STAR), Ingredient.fromItems(dank_6), Ingredient.fromItems(Items.NETHER_STAR),
              Ingredient.fromItems(Items.NETHER_STAR), Ingredient.fromItems(Items.NETHER_STAR),Ingredient.fromItems(Items.NETHER_STAR)), new ItemStack(dank_7));
    }

    @Nonnull
    @Override
    public IRecipeSerializer<?> getSerializer() {
      return upgrade6;
    }
  }

  public static class Tags {
    public static final Tag<Item> REDSTONE_BLOCK = tag("storage_blocks/redstone");
    public static final Tag<Item> GOLD_BLOCK = tag("storage_blocks/gold");
    public static final Tag<Item> EMERALD_BLOCK = tag("storage_blocks/emerald");
    public static final Tag<Item> DIAMOND_BLOCK = tag("storage_blocks/diamond");

    private static Tag<Item> tag(String name) {
      return new ItemTags.Wrapper(new ResourceLocation("forge", name));
    }
  }

}
