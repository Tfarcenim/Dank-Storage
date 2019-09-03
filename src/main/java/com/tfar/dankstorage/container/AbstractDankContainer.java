package com.tfar.dankstorage.container;

import com.tfar.dankstorage.inventory.DankHandler;
import com.tfar.dankstorage.tile.AbstractDankStorageTile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class AbstractDankContainer extends AbstractAbstractDankContainer {

  public AbstractDankStorageTile te;


  public AbstractDankContainer(World world, BlockPos pos, InventoryPlayer playerInventory, EntityPlayer player, DankHandler handler, int rows) {
    super(playerInventory,player,handler,rows);
    this.te = (AbstractDankStorageTile) world.getTileEntity(pos);
    te.openInventory(player);
  }

  @Override
  public void onContainerClosed(EntityPlayer playerIn) {
    super.onContainerClosed(playerIn);
    this.te.closeInventory(playerIn);
  }
}

