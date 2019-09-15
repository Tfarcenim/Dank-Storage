package com.tfar.dankstorage.screen;

import com.tfar.dankstorage.DankStorage;
import com.tfar.dankstorage.container.DankContainers;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;


public class DankScreens {

  private static final ResourceLocation background1 = new ResourceLocation(DankStorage.MODID,
          "textures/container/gui/dank1.png");

  private static final ResourceLocation background2 = new ResourceLocation(DankStorage.MODID,
          "textures/container/gui/dank2.png");

  private static final ResourceLocation background3 = new ResourceLocation("textures/gui/container/shulker_box.png");

  private static final ResourceLocation background4 = new ResourceLocation(DankStorage.MODID,
          "textures/container/gui/dank4.png");

  private static final ResourceLocation background5 = new ResourceLocation(DankStorage.MODID,
          "textures/container/gui/dank5.png");

  private static final ResourceLocation background6 = new ResourceLocation("textures/gui/container/generic_54.png");

  private static final ResourceLocation background7 = new ResourceLocation(DankStorage.MODID,
          "textures/container/gui/dank7.png");

  public static class DankStorageScreen1 extends AbstractDankStorageScreen<DankContainers.DankContainer1> {

    public DankStorageScreen1(DankContainers.DankContainer1 container, PlayerInventory playerinventory, ITextComponent component) {
      super(container, playerinventory, component, background1);
    }
  }

  public static class DankStorageScreen2 extends AbstractDankStorageScreen<DankContainers.DankContainer2> {

    public DankStorageScreen2(DankContainers.DankContainer2 container, PlayerInventory playerinventory, ITextComponent component) {
      super(container, playerinventory, component, background2);
    }
  }

  public static class DankStorageScreen3 extends AbstractDankStorageScreen<DankContainers.DankContainer3> {

    public DankStorageScreen3(DankContainers.DankContainer3 container, PlayerInventory playerinventory, ITextComponent component) {
      super(container, playerinventory, component, background3);
    }
  }

  public static class DankStorageScreen4 extends AbstractDankStorageScreen<DankContainers.DankContainer4> {

    public DankStorageScreen4(DankContainers.DankContainer4 container, PlayerInventory playerinventory, ITextComponent component) {
      super(container, playerinventory, component, background4);
    }
  }

  public static class DankStorageScreen5 extends AbstractDankStorageScreen<DankContainers.DankContainer5> {

    public DankStorageScreen5(DankContainers.DankContainer5 container, PlayerInventory playerinventory, ITextComponent component) {
      super(container, playerinventory, component, background5);
    }
  }

  public static class DankStorageScreen6 extends AbstractDankStorageScreen<DankContainers.DankContainer6> {

    public DankStorageScreen6(DankContainers.DankContainer6 container, PlayerInventory playerinventory, ITextComponent component) {
      super(container, playerinventory, component, background6);
    }
  }

  public static class DankStorageScreen7 extends AbstractDankStorageScreen<DankContainers.DankContainer7> {

    public DankStorageScreen7(DankContainers.DankContainer7 container, PlayerInventory playerinventory, ITextComponent component) {
      super(container, playerinventory, component, background7);
    }
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
      this.font.drawString(this.playerInventory.getDisplayName().getUnformattedComponentText(), 8, this.ySize - 105, 0x404040);
      this.font.drawString(this.te.getDisplayName().getUnformattedComponentText(), 8, 12, 4210752);
    }
  }

  public static class PortableDankStorageScreen1 extends AbstractPortableDankStorageScreen<DankContainers.PortableDankContainer1> {


    public PortableDankStorageScreen1(DankContainers.PortableDankContainer1 container, PlayerInventory playerinventory, ITextComponent component) {
      super(container,playerinventory, component, background1);
    }
  }

  public static class PortableDankStorageScreen2 extends AbstractPortableDankStorageScreen<DankContainers.PortableDankContainer2> {



    public PortableDankStorageScreen2(DankContainers.PortableDankContainer2 container, PlayerInventory playerinventory, ITextComponent component) {
      super(container, playerinventory, component, background2);
    }
  }

  public static class PortableDankStorageScreen3 extends AbstractPortableDankStorageScreen<DankContainers.PortableDankContainer3> {


    public PortableDankStorageScreen3(DankContainers.PortableDankContainer3 container, PlayerInventory playerinventory, ITextComponent component) {
      super(container, playerinventory, component, background3);
    }
  }

  public static class PortableDankStorageScreen4 extends AbstractPortableDankStorageScreen<DankContainers.PortableDankContainer4> {



    public PortableDankStorageScreen4(DankContainers.PortableDankContainer4 container, PlayerInventory playerinventory, ITextComponent component) {
      super(container, playerinventory, component, background4);
    }
  }

  public static class PortableDankStorageScreen5 extends AbstractPortableDankStorageScreen<DankContainers.PortableDankContainer5> {



    public PortableDankStorageScreen5(DankContainers.PortableDankContainer5 container, PlayerInventory playerinventory, ITextComponent component) {
      super(container, playerinventory, component, background5);
    }
  }

  public static class PortableDankStorageScreen6 extends AbstractPortableDankStorageScreen<DankContainers.PortableDankContainer6> {


    public PortableDankStorageScreen6(DankContainers.PortableDankContainer6 container, PlayerInventory playerinventory, ITextComponent component) {
      super(container, playerinventory, component, background6);
    }
  }

  public static class PortableDankStorageScreen7 extends AbstractPortableDankStorageScreen<DankContainers.PortableDankContainer7> {



    public PortableDankStorageScreen7(DankContainers.PortableDankContainer7 container, PlayerInventory playerinventory, ITextComponent component) {
      super(container, playerinventory, component, background7);
    }
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
      this.font.drawString(this.playerInventory.getDisplayName().getUnformattedComponentText(), 8, this.ySize - 105, 0x404040);
      this.font.drawString(this.bag.getDisplayName().getUnformattedComponentText(), 8, 12, 4210752);
    }
  }
}
