package com.tfar.dankstorage.screen;

import com.tfar.dankstorage.container.AbstractPortableDankContainer;
import com.tfar.dankstorage.network.CMessageTagMode;
import com.tfar.dankstorage.network.DankPacketHandler;
import com.tfar.dankstorage.network.Utils;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.CheckboxButton;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.client.config.GuiCheckBox;

public abstract class AbstractPortableDankStorageScreen<T extends AbstractPortableDankContainer> extends AbstractAbstractDankStorageScreen<T> {

  protected ItemStack bag;

  public AbstractPortableDankStorageScreen(T container, PlayerInventory playerinventory, ITextComponent component, ResourceLocation background) {
    super(container,playerinventory, component,background,container.rows);
    this.bag = playerinventory.player.getHeldItemMainhand();
  }

  @Override
  protected void init() {
    super.init();
    this.addButton(new SmallButton(guiLeft + 45, guiTop + 6,8,8, b -> {
      ((SmallButton)b).toggle();
      DankPacketHandler.INSTANCE.sendToServer(new CMessageTagMode());
    }, Utils.tag(bag)));
  }

  @Override
  protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
    super.drawGuiContainerForegroundLayer(mouseX,mouseY);
    this.font.drawString(this.bag.getDisplayName().getUnformattedComponentText(), 8, 6, 0x404040);
    this.font.drawString("Tag", 55, 6, 0x404040);

  }
}