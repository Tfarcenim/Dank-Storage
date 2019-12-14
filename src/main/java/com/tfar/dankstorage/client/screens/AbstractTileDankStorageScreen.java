package com.tfar.dankstorage.client.screens;

import com.tfar.dankstorage.container.AbstractTileDankContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public abstract class AbstractTileDankStorageScreen<T extends AbstractTileDankContainer> extends AbstractAbstractDankStorageScreen<T> {

  public AbstractTileDankStorageScreen(T container, PlayerInventory playerinventory, ITextComponent component, ResourceLocation background) {
    super(container,playerinventory, component,background);
  }

  @Override
  public ITextComponent getContainerName() {
    return container.te.getDisplayName();
  }
}