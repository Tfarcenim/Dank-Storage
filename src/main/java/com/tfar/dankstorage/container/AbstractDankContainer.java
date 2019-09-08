package com.tfar.dankstorage.container;

import com.tfar.dankstorage.inventory.DankHandler;
import com.tfar.dankstorage.tile.AbstractDankStorageTile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class AbstractDankContainer extends AbstractAbstractDankContainer {

  public AbstractDankStorageTile te;


  public AbstractDankContainer(ContainerType<?> type, int p_i50105_2_, World world, BlockPos pos, PlayerInventory playerInventory, PlayerEntity player,DankHandler handler, int rows) {
    super(type, p_i50105_2_, playerInventory, handler,rows);
    this.te = (AbstractDankStorageTile) world.getTileEntity(pos);
    te.openInventory(player);
  }

  @Override
  public void onContainerClosed(PlayerEntity playerIn) {
    super.onContainerClosed(playerIn);
    this.te.closeInventory(playerIn);
  }
}

