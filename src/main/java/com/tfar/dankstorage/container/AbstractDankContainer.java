package com.tfar.dankstorage.container;

import com.tfar.dankstorage.inventory.DankHandler;
import com.tfar.dankstorage.tile.AbstractDankStorageTile;
import com.tfar.dankstorage.utils.Utils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.wrapper.InvWrapper;

public abstract class AbstractDankContainer extends AbstractAbstractDankContainer {

  public AbstractDankStorageTile te;


  public AbstractDankContainer(ContainerType<?> type, int p_i50105_2_, World world, BlockPos pos, PlayerInventory playerInventory, PlayerEntity player, int rows) {
    super(type, p_i50105_2_, playerInventory,rows);
    te = (AbstractDankStorageTile) world.getTileEntity(pos);
    te.openInventory(player);
    addOwnSlots();
    addPlayerSlots(new InvWrapper(playerInventory));
  }

  @Override
  public DankHandler getHandler() {
    return te.getHandler();
  }

  @Override
  public void onContainerClosed(PlayerEntity playerIn) {
    super.onContainerClosed(playerIn);
    this.te.closeInventory(playerIn);
  }
}

