package com.tfar.dankstorage.container;

import com.tfar.dankstorage.DankStorage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.world.World;

public class PortableDankContainer7 extends AbstractPortableDankContainer {

  public PortableDankContainer7(int p_i50105_2_, World world, PlayerInventory playerInventory, PlayerEntity player) {
    super(DankStorage.Objects.portable_dank_7_container, p_i50105_2_,world,playerInventory,player,9,Integer.MAX_VALUE);
  }
}

