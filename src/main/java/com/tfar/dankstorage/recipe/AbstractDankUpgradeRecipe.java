package com.tfar.dankstorage.recipe;

import com.tfar.dankstorage.DankStorage;
import com.tfar.dankstorage.inventory.PortableDankHandler;
import com.tfar.dankstorage.network.Utils;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public abstract class AbstractDankUpgradeRecipe extends ShapedRecipe {

  public AbstractDankUpgradeRecipe(ResourceLocation idIn,NonNullList<Ingredient> ingredients,ItemStack result) {
    super(idIn,DankStorage.MODID, 3, 3, ingredients,result);
  }

  @Nonnull
  @Override
  public ItemStack getCraftingResult(InventoryCrafting inv) {
    ItemStack bag = super.getCraftingResult(inv).copy();
    ItemStack oldBag = inv.getStackInSlot(4);
    PortableDankHandler oldHandler = Utils.getHandler(oldBag);
    PortableDankHandler newHandler = Utils.getHandler(bag);
    for (int i = 0;i < oldHandler.getSlots();i++){
      newHandler.insertItem(i,oldHandler.getStackInSlot(i),false);
    }
    NBTTagCompound nbt = newHandler.serializeNBT();
    bag.setTagCompound(nbt);
    return bag;
  }
}
