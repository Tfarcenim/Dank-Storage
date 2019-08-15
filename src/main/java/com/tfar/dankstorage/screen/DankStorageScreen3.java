package com.tfar.dankstorage.screen;

import com.tfar.dankstorage.container.DankContainer3;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

import static com.tfar.dankstorage.screen.PortableDankStorageScreen3.background3;

public class DankStorageScreen3 extends AbstractDankStorageScreen<DankContainer3> {

  public DankStorageScreen3(DankContainer3 container, PlayerInventory playerinventory, ITextComponent component) {
    super(container, playerinventory, component, background3,3);
  }
}
