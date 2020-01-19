package tfar.dankstorage.recipe;

import tfar.dankstorage.DankStorage;
import tfar.dankstorage.utils.Utils;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.ShapedRecipe;

import javax.annotation.Nonnull;

public class UpgradeRecipe extends ShapedRecipe {

  public UpgradeRecipe(ShapedRecipe recipe){
    super(recipe.getId(),recipe.getGroup(),recipe.getWidth(),recipe.getHeight(),recipe.getIngredients(),recipe.getRecipeOutput());
  }

  @Nonnull
  @Override
  public ItemStack getCraftingResult(CraftingInventory inv) {
    ItemStack bag = super.getCraftingResult(inv).copy();
    ItemStack oldBag = inv.getStackInSlot(4).copy();
    if (!oldBag.hasTag())return bag;
    bag.setTag(oldBag.getTag());
    bag.getOrCreateChildTag(Utils.INV).putInt("Size",Utils.getSlotCount(bag));
    return bag;
  }

  @Override
  public IRecipeSerializer<?> getSerializer() {
    return DankStorage.Objects.upgrade;
  }
}
