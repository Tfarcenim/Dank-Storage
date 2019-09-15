package com.tfar.dankstorage.block;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class ItemUseContextExt extends ItemUseContext {
  protected ItemUseContextExt(World p_i50034_1_, @Nullable PlayerEntity p_i50034_2_, Hand p_i50034_3_, ItemStack p_i50034_4_, BlockRayTraceResult p_i50034_5_) {
    super(p_i50034_1_, p_i50034_2_, p_i50034_3_, p_i50034_4_, p_i50034_5_);
  }
}
