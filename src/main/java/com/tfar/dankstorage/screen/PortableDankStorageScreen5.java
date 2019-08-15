package com.tfar.dankstorage.screen;

import com.tfar.dankstorage.DankStorage;
import com.tfar.dankstorage.container.PortableDankContainer5;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class PortableDankStorageScreen5 extends AbstractPortableDankStorageScreen<PortableDankContainer5> {

  static final ResourceLocation background5 = new ResourceLocation(DankStorage.MODID,
          "textures/container/gui/dank5.png");

  public PortableDankStorageScreen5(PortableDankContainer5 container, PlayerInventory playerinventory, ITextComponent component) {
    super(container, playerinventory, component, background5);
  }
}
