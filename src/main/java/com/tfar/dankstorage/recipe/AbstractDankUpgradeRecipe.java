package com.tfar.dankstorage.recipe;

import com.tfar.dankstorage.DankStorage;
import com.tfar.dankstorage.inventory.PortableDankHandler;
import com.tfar.dankstorage.network.Utils;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public abstract class AbstractDankUpgradeRecipe extends ShapedRecipe {

  public AbstractDankUpgradeRecipe(ResourceLocation idIn,NonNullList<Ingredient> ingredients,ItemStack result) {
    super(idIn,DankStorage.MODID, 3, 3, ingredients,result);
  }

  @Nonnull
  @Override
  public ItemStack getCraftingResult(CraftingInventory inv) {
    ItemStack bag = super.getCraftingResult(inv).copy();
    ItemStack oldBag = inv.getStackInSlot(4).copy();
    if (!oldBag.hasTag())return bag;
    bag.setTag(oldBag.getTag());
    bag.getTag().putInt("Size",Utils.getSlotCount(bag));
    return bag;
  }
}
