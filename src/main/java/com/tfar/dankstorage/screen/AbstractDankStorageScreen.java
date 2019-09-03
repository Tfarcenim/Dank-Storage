package com.tfar.dankstorage.screen;

import com.tfar.dankstorage.container.AbstractDankContainer;
import com.tfar.dankstorage.tile.AbstractDankStorageTile;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public abstract class AbstractDankStorageScreen<T extends AbstractDankContainer> extends AbstractAbstractDankStorageScreen<T> {

  protected AbstractDankStorageTile te;

  public AbstractDankStorageScreen(T container, InventoryPlayer playerinventory, ResourceLocation background) {
    super(container,playerinventory,background,container.rows);
    this.te = container.te;
  }

  @Override
  protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
    super.drawGuiContainerForegroundLayer(mouseX,mouseY);
    this.fontRenderer.drawString(this.te.getDisplayName().getUnformattedComponentText(), 8, 6, 4210752);
  }
}