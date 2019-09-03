package com.tfar.dankstorage.container;

import com.tfar.dankstorage.inventory.DankHandler;
import com.tfar.dankstorage.tile.AbstractDankStorageTile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.MenuType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class AbstractDankContainer extends AbstractAbstractDankContainer {

  public AbstractDankStorageTile te;


  public AbstractDankContainer(MenuType<?> type, int p_i50105_2_, World world, BlockPos pos, InventoryPlayer playerInventory, EntityPlayer player, DankHandler handler, int rows) {
    super(type, p_i50105_2_, playerInventory,player,handler,rows);
    this.te = (AbstractDankStorageTile) world.getTileEntity(pos);
    te.openInventory(player);
  }

  @Override
  public void onContainerClosed(EntityPlayer playerIn) {
    super.onContainerClosed(playerIn);
    this.te.closeInventory(playerIn);
  }
}

