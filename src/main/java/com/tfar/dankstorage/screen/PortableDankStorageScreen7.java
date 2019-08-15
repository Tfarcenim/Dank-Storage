package com.tfar.dankstorage.screen;

import com.tfar.dankstorage.DankStorage;
import com.tfar.dankstorage.container.PortableDankContainer7;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class PortableDankStorageScreen7 extends AbstractPortableDankStorageScreen<PortableDankContainer7> {

  static final ResourceLocation background7 = new ResourceLocation(DankStorage.MODID,
          "textures/container/gui/dank7.png");

  public PortableDankStorageScreen7(PortableDankContainer7 container, PlayerInventory playerinventory, ITextComponent component) {
    super(container, playerinventory, component, background7);
  }
}
