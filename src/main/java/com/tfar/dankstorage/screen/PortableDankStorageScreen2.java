package com.tfar.dankstorage.screen;

import com.tfar.dankstorage.DankStorage;
import com.tfar.dankstorage.container.PortableDankContainer2;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class PortableDankStorageScreen2 extends AbstractPortableDankStorageScreen<PortableDankContainer2> {

  static final ResourceLocation background2 = new ResourceLocation(DankStorage.MODID,
          "textures/container/gui/dank2.png");

  public PortableDankStorageScreen2(PortableDankContainer2 container, PlayerInventory playerinventory, ITextComponent component) {
    super(container, playerinventory, component, background2);
  }
}
