package com.tfar.dankstorage.screen;

import com.tfar.dankstorage.container.DankContainer4;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

import static com.tfar.dankstorage.screen.PortableDankStorageScreen4.background4;

public class DankStorageScreen4 extends AbstractDankStorageScreen<DankContainer4> {

  public DankStorageScreen4(DankContainer4 container, PlayerInventory playerinventory, ITextComponent component) {
    super(container, playerinventory, component, background4,4);
  }
}
