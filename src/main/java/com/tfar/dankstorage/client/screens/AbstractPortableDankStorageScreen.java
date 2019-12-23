package com.tfar.dankstorage.client.screens;

import com.tfar.dankstorage.client.button.RedGreenToggleButton;
import com.tfar.dankstorage.client.button.TripleToggleButton;
import com.tfar.dankstorage.container.AbstractPortableDankContainer;
import com.tfar.dankstorage.network.C2SMessageTagMode;
import com.tfar.dankstorage.network.CMessageTogglePickup;
import com.tfar.dankstorage.network.DankPacketHandler;
import com.tfar.dankstorage.utils.Utils;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public abstract class AbstractPortableDankStorageScreen<T extends AbstractPortableDankContainer> extends AbstractAbstractDankStorageScreen<T> {

  public AbstractPortableDankStorageScreen(T container, PlayerInventory playerinventory, ITextComponent component, ResourceLocation background) {
    super(container,playerinventory, component,background);
  }

  @Override
  protected void init() {
    super.init();
    int start = 0;
    int namelength = font.getStringWidth(getContainerName().getUnformattedComponentText());
    start += namelength;
    this.addButton(new RedGreenToggleButton(guiLeft + (start += 16), guiTop + 6 ,8,8, b -> {
      ((RedGreenToggleButton)b).toggle();
      DankPacketHandler.INSTANCE.sendToServer(new C2SMessageTagMode());
    }, Utils.oredict(container.getBag())));
    this.addButton(new TripleToggleButton(guiLeft + (start += 30), guiTop + 6 ,8,8, b -> {
      ((TripleToggleButton)b).toggle();
      DankPacketHandler.INSTANCE.sendToServer(new CMessageTogglePickup());
    }, Utils.getMode(container.getBag()).ordinal()));
  }

  @Override
  public ITextComponent getContainerName() {
    return container.getBag().getDisplayName();
  }

  @Override
  protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
    super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    int namelength = font.getStringWidth(getContainerName().getUnformattedComponentText());
    this.font.drawString("Tag", 25 + namelength, 6, 0x404040);
    this.font.drawString("Pickup", 56 + namelength, 6 , 0x404040);
    int color = container.nbtSize > 2000000 ? 0x800000 : 0x008000;
    this.font.drawString("NBT: " + container.nbtSize,70,this.ySize - 94,color);
  }
}