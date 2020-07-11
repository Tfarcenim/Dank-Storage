package tfar.dankstorage.client.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.inventory.container.PlayerContainer;
import tfar.dankstorage.client.button.RedGreenToggleButton;
import tfar.dankstorage.client.button.TripleToggleButton;
import tfar.dankstorage.container.AbstractPortableDankContainer;
import tfar.dankstorage.network.C2SMessageTagMode;
import tfar.dankstorage.network.CMessageTogglePickup;
import tfar.dankstorage.network.DankPacketHandler;
import tfar.dankstorage.utils.Utils;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class PortableDankStorageScreen extends AbstractAbstractDankStorageScreen<AbstractPortableDankContainer> {

  public PortableDankStorageScreen(AbstractPortableDankContainer container, PlayerInventory playerinventory, ITextComponent component, ResourceLocation background) {
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
  protected void func_230451_b_(MatrixStack stack,int mouseX, int mouseY) {
    super.func_230451_b_(stack,mouseX, mouseY);
    int namelength = font.getStringWidth(getContainerName().getUnformattedComponentText());
    this.font.drawString(stack,"Tag", 25 + namelength, 6, 0x404040);
    this.font.drawString(stack,"Pickup", 56 + namelength, 6 , 0x404040);
    int color = container.nbtSize > 2000000 ? 0x800000 : 0x008000;
    this.font.drawString(stack,"NBT: " + container.nbtSize,70,this.ySize - 94,color);
  }

  public static PortableDankStorageScreen t1(AbstractPortableDankContainer container,PlayerInventory playerinventory, ITextComponent component) {
    return new PortableDankStorageScreen(container,playerinventory,component,DankScreens.background1);
  }

  public static PortableDankStorageScreen t2(AbstractPortableDankContainer container,PlayerInventory playerinventory, ITextComponent component) {
    return new PortableDankStorageScreen(container,playerinventory,component,DankScreens.background2);
  }

  public static PortableDankStorageScreen t3(AbstractPortableDankContainer container,PlayerInventory playerinventory, ITextComponent component) {
    return new PortableDankStorageScreen(container,playerinventory,component,DankScreens.background3);
  }

  public static PortableDankStorageScreen t4(AbstractPortableDankContainer container,PlayerInventory playerinventory, ITextComponent component) {
    return new PortableDankStorageScreen(container,playerinventory,component,DankScreens.background4);
  }

  public static PortableDankStorageScreen t5(AbstractPortableDankContainer container,PlayerInventory playerinventory, ITextComponent component) {
    return new PortableDankStorageScreen(container,playerinventory,component,DankScreens.background5);
  }

  public static PortableDankStorageScreen t6(AbstractPortableDankContainer container,PlayerInventory playerinventory, ITextComponent component) {
    return new PortableDankStorageScreen(container,playerinventory,component,DankScreens.background6);
  }

  public static PortableDankStorageScreen t7(AbstractPortableDankContainer container,PlayerInventory playerinventory, ITextComponent component) {
    return new PortableDankStorageScreen(container,playerinventory,component,DankScreens.background7);
  }

}