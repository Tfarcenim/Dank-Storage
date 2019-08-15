package com.tfar.dankstorage.container;

import com.tfar.dankstorage.DankStorage;
import com.tfar.dankstorage.tile.DankStorageTile1;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DankContainer2 extends AbstractDankContainer {

  public DankContainer2(int p_i50105_2_, World world, BlockPos pos, PlayerInventory playerInventory, PlayerEntity player) {
    super(DankStorage.Objects.dank_2_container, p_i50105_2_,world,pos,playerInventory,player,2);
  }
}

