package com.tfar.dankstorage.screen;

import com.tfar.dankstorage.DankStorage;
import com.tfar.dankstorage.container.PortableDankContainer4;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class PortableDankStorageScreen4 extends AbstractPortableDankStorageScreen<PortableDankContainer4> {

  static final ResourceLocation background4 = new ResourceLocation(DankStorage.MODID,
          "textures/container/gui/dank4.png");

  public PortableDankStorageScreen4(PortableDankContainer4 container, PlayerInventory playerinventory, ITextComponent component) {
    super(container, playerinventory, component, background4);
  }
}
