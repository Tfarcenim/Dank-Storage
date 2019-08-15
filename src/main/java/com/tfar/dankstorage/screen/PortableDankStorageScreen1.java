package com.tfar.dankstorage.screen;

import com.tfar.dankstorage.DankStorage;
import com.tfar.dankstorage.container.PortableDankContainer1;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class PortableDankStorageScreen1 extends AbstractPortableDankStorageScreen<PortableDankContainer1> {

  static final ResourceLocation background1 = new ResourceLocation(DankStorage.MODID,
          "textures/container/gui/dank1.png");
  public PortableDankStorageScreen1(PortableDankContainer1 container, PlayerInventory playerinventory, ITextComponent component) {
    super(container,playerinventory, component, background1);
  }
}
