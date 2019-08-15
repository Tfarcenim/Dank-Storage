package com.tfar.dankstorage.screen;

import com.tfar.dankstorage.container.DankContainer5;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

import static com.tfar.dankstorage.screen.PortableDankStorageScreen5.background5;

public class DankStorageScreen5 extends AbstractDankStorageScreen<DankContainer5> {

  public DankStorageScreen5(DankContainer5 container, PlayerInventory playerinventory, ITextComponent component) {
    super(container, playerinventory, component, background5,5);
  }
}
