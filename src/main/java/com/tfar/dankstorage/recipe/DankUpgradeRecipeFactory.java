package com.tfar.dankstorage.recipe;

import com.google.gson.JsonObject;
import com.tfar.dankstorage.DankStorage;
import com.tfar.dankstorage.inventory.PortableDankHandler;
import com.tfar.dankstorage.util.Utils;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.oredict.ShapedOreRecipe;

import javax.annotation.Nonnull;

public class DankUpgradeRecipeFactory implements IRecipeFactory {

  @Override
  public IRecipe parse(JsonContext context, JsonObject json) {
    ShapedOreRecipe recipe = ShapedOreRecipe.factory(context, json);

    CraftingHelper.ShapedPrimer primer = new CraftingHelper.ShapedPrimer();
    primer.width = recipe.getRecipeWidth();
    primer.height = recipe.getRecipeHeight();
    primer.mirrored = JsonUtils.getBoolean(json, "mirrored", true);
    primer.input = recipe.getIngredients();

    return new DankRecipe(new ResourceLocation(DankStorage.MODID, "upgrade"), recipe.getRecipeOutput(), primer);  }


  public static class DankRecipe extends ShapedOreRecipe {
    public DankRecipe(ResourceLocation group, ItemStack result, CraftingHelper.ShapedPrimer primer) {
      super(group, result, primer);
    }

    @Override
    @Nonnull
    public ItemStack getCraftingResult(@Nonnull InventoryCrafting inv) {

      ItemStack bag = super.getCraftingResult(inv).copy();
      ItemStack oldBag = inv.getStackInSlot(4);
      PortableDankHandler oldHandler = Utils.getHandler(oldBag);
      PortableDankHandler newHandler = Utils.getHandler(bag);
      for (int i = 0; i < oldHandler.getSlots(); i++) {
        newHandler.insertItem(i, oldHandler.getStackInSlot(i), false);
      }
      NBTTagCompound nbt = newHandler.serializeNBT();
      bag.setTagCompound(nbt);
      return bag;
    }
  }
}
