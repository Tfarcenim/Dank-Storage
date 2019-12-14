package com.tfar.dankstorage.item;

import com.tfar.dankstorage.block.DankBlock;
import com.tfar.dankstorage.tile.AbstractDankStorageTile;
import com.tfar.dankstorage.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class UpgradeItem extends Item {

  protected final UpgradeInfo upgradeInfo;

  public UpgradeItem(Properties properties, UpgradeInfo info) {
    super(properties);
    this.upgradeInfo = info;
  }

  @Nonnull
  @Override
  public ActionResultType onItemUse(ItemUseContext context) {
    PlayerEntity player = context.getPlayer();
    BlockPos pos = context.getPos();
    World world = context.getWorld();
    ItemStack upgradeStack = context.getItem();
    BlockState state = world.getBlockState(pos);

    if (player == null || !(state.getBlock() instanceof DankBlock) || !upgradeInfo.canUpgrade((DankBlock) state.getBlock())) {
      return ActionResultType.FAIL;
    }
    if (world.isRemote)
      return ActionResultType.PASS;

      if (false) {
        player.sendStatusMessage(new TranslationTextComponent("metalbarrels.in_use")
                .setStyle(new Style().setColor(TextFormatting.RED)), true);
        return ActionResultType.PASS;
  }

    TileEntity oldDank = world.getTileEntity(pos);

    //shortcut
    final List<ItemStack> oldDankContents = new ArrayList<>(((AbstractDankStorageTile) oldDank).getHandler().getContents());

    oldDank.remove();

    Block newBlock = Utils.getBlockFromTier(upgradeInfo.end);

    BlockState newState = newBlock.getDefaultState();

    world.setBlockState(pos, newState, 3);
    TileEntity newBarrel = world.getTileEntity(pos);

    newBarrel.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(itemHandler -> IntStream.range(0, oldDankContents.size()).forEach(i -> itemHandler.insertItem(i, oldDankContents.get(i), false)));

    if (!player.abilities.isCreativeMode)
      upgradeStack.shrink(1);

    player.sendStatusMessage(new TranslationTextComponent("metalbarrels.upgrade_successful")
            .setStyle(new Style().setColor(TextFormatting.GREEN)), true);
    return ActionResultType.SUCCESS;
  }
}