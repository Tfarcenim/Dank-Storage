package com.tfar.dankstorage.container;

import com.tfar.dankstorage.DankStorage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.world.World;

public class PortableDankContainer3 extends AbstractPortableDankContainer {

  public PortableDankContainer3(int p_i50105_2_, World world, PlayerInventory playerInventory, PlayerEntity player) {
    super(DankStorage.Objects.portable_dank_3_container, p_i50105_2_,world,playerInventory,player,3,4096);
  }
}

