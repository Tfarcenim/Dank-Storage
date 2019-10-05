package com.tfar.dankstorage.screen;

import com.tfar.dankstorage.container.AbstractPortableDankContainer;
import com.tfar.dankstorage.network.CMessageTagMode;
import com.tfar.dankstorage.network.CMessageTogglePickup;
import com.tfar.dankstorage.network.DankPacketHandler;
import com.tfar.dankstorage.network.Utils;
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
  protected void init() {
    super.init();
    int start = 0;
    this.addButton(new ToggleButton(guiLeft + (start += 45), guiTop + 6,8,8, b -> {
      ((ToggleButton)b).toggle();
      DankPacketHandler.INSTANCE.sendToServer(new CMessageTagMode());
    }, Utils.tag(bag)));
    this.addButton(new TripleToggleButton(guiLeft + (start += 40), guiTop + 6,8,8, b -> {
      ((TripleToggleButton)b).toggle();
      DankPacketHandler.INSTANCE.sendToServer(new CMessageTogglePickup());
    }, Utils.getMode(bag).ordinal()));
  }

  @Override
  protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
    super.drawGuiContainerForegroundLayer(mouseX,mouseY);
    this.font.drawString(this.bag.getDisplayName().getUnformattedComponentText(), 8, 6, 0x404040);
    this.font.drawString("Tag", 55, 6, 0x404040);
    this.font.drawString("Pickup", 95, 6, 0x404040);
  }
}