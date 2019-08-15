package com.tfar.dankstorage.screen;

import com.tfar.dankstorage.container.PortableDankContainer6;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class PortableDankStorageScreen6 extends AbstractPortableDankStorageScreen<PortableDankContainer6> {

  static final ResourceLocation background6 = new ResourceLocation("textures/gui/container/generic_54.png");

  public PortableDankStorageScreen6(PortableDankContainer6 container, PlayerInventory playerinventory, ITextComponent component) {
    super(container, playerinventory, component, background6);
  }
}
