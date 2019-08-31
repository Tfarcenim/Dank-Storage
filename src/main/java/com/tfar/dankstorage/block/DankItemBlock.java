package com.tfar.dankstorage.block;

import com.tfar.dankstorage.container.PortableDankProvider;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class DankItemBlock extends BlockItem {
  public DankItemBlock(Block p_i48527_1_, Properties p_i48527_2_) {
    super(p_i48527_1_, p_i48527_2_);
  }

  public static final Rarity GRAY = Rarity.create("dark_gray", TextFormatting.GRAY);
  public static final Rarity RED = Rarity.create("red", TextFormatting.RED);
  public static final Rarity GOLD = Rarity.create("gold", TextFormatting.GOLD);
  public static final Rarity GREEN = Rarity.create("green", TextFormatting.GREEN);
  public static final Rarity BLUE = Rarity.create("blue", TextFormatting.AQUA);
  public static final Rarity PURPLE = Rarity.create("purple", TextFormatting.DARK_PURPLE);
  public static final Rarity WHITE = Rarity.create("white", TextFormatting.WHITE);

  @Override
  public Rarity getRarity(ItemStack stack) {
    int type = Integer.parseInt(this.getRegistryName().getPath().substring(5));
    switch (type){
      case 1: return GRAY;
      case 2:return RED;
      case 3:return GOLD;
      case 4:return GREEN;
      case 5:return BLUE;
      case 6:return PURPLE;
      case 7:return WHITE;
    }
    return super.getRarity(stack);
  }

  @Override
  public ActionResult<ItemStack> onItemRightClick(World p_77659_1_, PlayerEntity player, Hand hand) {
    if (!p_77659_1_.isRemote) {
      int type = Integer.parseInt(player.getHeldItem(hand).getItem().getRegistryName().getPath().substring(5));
      NetworkHooks.openGui((ServerPlayerEntity) player, new PortableDankProvider(type), data -> data.writeItemStack(player.getHeldItem(hand)));
    }
    return super.onItemRightClick(p_77659_1_, player, hand);
  }
}
