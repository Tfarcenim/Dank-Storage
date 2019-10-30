package com.tfar.dankstorage.screen;

import com.tfar.dankstorage.container.AbstractDankContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public abstract class AbstractDankStorageScreen<T extends AbstractDankContainer> extends AbstractAbstractDankStorageScreen<T> {


  public AbstractDankStorageScreen(T container, PlayerInventory playerinventory, ITextComponent component, ResourceLocation background) {
    super(container,playerinventory, component,background);
  }

  @Override
  public ITextComponent getContainerName() {
    return container.te.getDisplayName();
  }
}