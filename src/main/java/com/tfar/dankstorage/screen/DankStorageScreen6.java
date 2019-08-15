package com.tfar.dankstorage.screen;

import com.tfar.dankstorage.container.DankContainer6;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

import static com.tfar.dankstorage.screen.PortableDankStorageScreen6.background6;

public class DankStorageScreen6 extends AbstractDankStorageScreen<DankContainer6> {

  public DankStorageScreen6(DankContainer6 container, PlayerInventory playerinventory, ITextComponent component) {
    super(container, playerinventory, component, background6,6);
  }
}
