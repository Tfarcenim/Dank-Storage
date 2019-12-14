package com.tfar.dankstorage.recipe;

import com.google.gson.JsonObject;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class Serializer2 extends ShapedRecipe.Serializer {
   @Override
    public UpgradeRecipe read(ResourceLocation location, JsonObject json) {
      return new UpgradeRecipe(super.read(location,json));
   }

  @Override
  @Nonnull
  @SuppressWarnings("ConstantConditions")
  public UpgradeRecipe read(@Nonnull ResourceLocation p_199426_1_, PacketBuffer p_199426_2_) {
    return new UpgradeRecipe(super.read(p_199426_1_, p_199426_2_));
  }
}