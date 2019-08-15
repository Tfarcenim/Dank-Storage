package com.tfar.dankstorage.screen;

import com.tfar.dankstorage.container.DankContainer2;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

import static com.tfar.dankstorage.screen.PortableDankStorageScreen2.background2;

public class DankStorageScreen2 extends AbstractDankStorageScreen<DankContainer2> {

  public DankStorageScreen2(DankContainer2 container, PlayerInventory playerinventory, ITextComponent component) {
    super(container, playerinventory, component, background2,2);
  }
}
