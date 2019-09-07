package com.tfar.dankstorage.screen;

import com.tfar.dankstorage.container.AbstractPortableDankContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public abstract class AbstractPortableDankStorageScreen<T extends AbstractPortableDankContainer> extends AbstractAbstractDankStorageScreen<T> {

  protected ItemStack bag;

  public AbstractPortableDankStorageScreen(T container, InventoryPlayer playerinventory, ResourceLocation background) {
    super(container,playerinventory, background,container.rows);
    this.bag = playerinventory.player.getHeldItemMainhand();
  }

  @Override
  protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
    super.drawGuiContainerForegroundLayer(mouseX,mouseY);
    this.fontRenderer.drawString(this.bag.getDisplayName(), 6, 11, 0x404040);
  }
}