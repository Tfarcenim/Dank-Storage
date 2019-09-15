package com.tfar.dankstorage.screen;

import com.tfar.dankstorage.container.AbstractDankContainer;
import com.tfar.dankstorage.tile.AbstractDankStorageTile;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public abstract class AbstractDankStorageScreen<T extends AbstractDankContainer> extends AbstractAbstractDankStorageScreen<T> {

  protected AbstractDankStorageTile te;

  public AbstractDankStorageScreen(T container, PlayerInventory playerinventory, ITextComponent component, ResourceLocation background) {
    super(container,playerinventory, component,background,container.rows);
    this.te = container.te;
  }

  @Override
  protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
    super.drawGuiContainerForegroundLayer(mouseX,mouseY);
    this.font.drawString(this.te.getDisplayName().getUnformattedComponentText(), 8, 6, 4210752);
  }
}