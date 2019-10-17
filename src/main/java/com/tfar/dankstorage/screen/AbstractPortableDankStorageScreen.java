package com.tfar.dankstorage.screen;

import com.tfar.dankstorage.container.AbstractPortableDankContainer;
import com.tfar.dankstorage.network.C2SMessageTagMode;
import com.tfar.dankstorage.network.CMessageTogglePickup;
import com.tfar.dankstorage.network.DankPacketHandler;
import com.tfar.dankstorage.utils.Utils;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public abstract class AbstractPortableDankStorageScreen<T extends AbstractPortableDankContainer> extends AbstractAbstractDankStorageScreen<T> {

  public AbstractPortableDankStorageScreen(T container, PlayerInventory playerinventory, ITextComponent component, ResourceLocation background) {
    super(container,playerinventory, component,background,container.rows);
  }

  @Override
  protected void init() {
    super.init();
    int start = 0;
    this.addButton(new ToggleButton(guiLeft + (start += 45), guiTop + 6,8,8, b -> {
      ((ToggleButton)b).toggle();
      DankPacketHandler.INSTANCE.sendToServer(new C2SMessageTagMode());
    }, Utils.tag(container.getBag())));
    this.addButton(new TripleToggleButton(guiLeft + (start += 40), guiTop + 6,8,8, b -> {
      ((TripleToggleButton)b).toggle();
      DankPacketHandler.INSTANCE.sendToServer(new CMessageTogglePickup());
    }, Utils.getMode(container.getBag()).ordinal()));
  }

  @Override
  protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
    super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    this.font.drawString(this.container.getBag().getDisplayName().getUnformattedComponentText(), 8, 6, 0x404040);
    this.font.drawString("Tag", 55, 6, 0x404040);
    this.font.drawString("Pickup", 95, 6, 0x404040);
    int color = container.nbtSize > 2000000 ? 0x800000 : 0x008000;
    this.font.drawString("NBT: "+container.nbtSize,70,this.ySize - 110,color);
  }
}