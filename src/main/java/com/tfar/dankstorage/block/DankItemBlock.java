package com.tfar.dankstorage.block;

import com.tfar.dankstorage.container.PortableDankProvider;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class DankItemBlock extends BlockItem {
  public DankItemBlock(Block p_i48527_1_, Properties p_i48527_2_) {
    super(p_i48527_1_, p_i48527_2_);
  }

  @Override
  public ActionResult<ItemStack> onItemRightClick(World p_77659_1_, PlayerEntity p_77659_2_, Hand p_77659_3_) {
    if (!p_77659_1_.isRemote) {
      int type = Integer.parseInt(p_77659_2_.getHeldItem(p_77659_3_).getItem().getRegistryName().getPath().substring(5));
      NetworkHooks.openGui((ServerPlayerEntity) p_77659_2_, new PortableDankProvider(type));
    }
    return super.onItemRightClick(p_77659_1_, p_77659_2_, p_77659_3_);
  }
}
