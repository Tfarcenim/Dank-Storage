package com.tfar.dankstorage.screen;

import com.tfar.dankstorage.container.AbstractPortableDankContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public abstract class AbstractPortableDankStorageScreen<T extends AbstractPortableDankContainer> extends AbstractAbstractDankStorageScreen<T> {

  protected ItemStack bag;

  public AbstractPortableDankStorageScreen(T container, PlayerInventory playerinventory, ITextComponent component, ResourceLocation background) {
    super(container,playerinventory, component,background,container.rows);
    this.bag = playerinventory.player.getHeldItemMainhand();
  }

  @Override
  protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
    super.drawGuiContainerForegroundLayer(mouseX,mouseY);
    this.font.drawString(this.bag.getDisplayName().getUnformattedComponentText(), 8, 6, 0x404040);
  }
}