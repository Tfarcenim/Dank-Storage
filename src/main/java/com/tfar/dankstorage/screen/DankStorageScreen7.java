package com.tfar.dankstorage.screen;

import com.tfar.dankstorage.container.DankContainer7;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

import static com.tfar.dankstorage.screen.PortableDankStorageScreen7.background7;

public class DankStorageScreen7 extends AbstractDankStorageScreen<DankContainer7> {

  public DankStorageScreen7(DankContainer7 container, PlayerInventory playerinventory, ITextComponent component) {
    super(container, playerinventory, component, background7,9);
  }
}
