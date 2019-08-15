package com.tfar.dankstorage.container;

import com.tfar.dankstorage.DankStorage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.world.World;

public class PortableDankContainer6 extends AbstractPortableDankContainer {

  public PortableDankContainer6(int p_i50105_2_, World world, PlayerInventory playerInventory, PlayerEntity player) {
    super(DankStorage.Objects.portable_dank_6_container, p_i50105_2_,world,playerInventory,player,6,262144);
  }
}

