package com.tfar.dankstorage.screen;

import com.tfar.dankstorage.container.PortableDankContainer3;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class PortableDankStorageScreen3 extends AbstractPortableDankStorageScreen<PortableDankContainer3> {

  static final ResourceLocation background3 = new ResourceLocation("textures/gui/container/shulker_box.png");

  public PortableDankStorageScreen3(PortableDankContainer3 container, PlayerInventory playerinventory, ITextComponent component) {
    super(container, playerinventory, component, background3);
  }
}
