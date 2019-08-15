package com.tfar.dankstorage.screen;

import com.tfar.dankstorage.container.DankContainer1;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

import static com.tfar.dankstorage.screen.PortableDankStorageScreen1.background1;

public class DankStorageScreen1 extends AbstractDankStorageScreen<DankContainer1> {

  public DankStorageScreen1(DankContainer1 container, PlayerInventory playerinventory, ITextComponent component) {
    super(container, playerinventory, component, background1,1);
  }
}
