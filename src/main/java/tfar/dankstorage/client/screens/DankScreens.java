package tfar.dankstorage.client.screens;

import tfar.dankstorage.DankStorage;
import tfar.dankstorage.container.DankContainers;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;


public class DankScreens {

  private static final ResourceLocation background1 = new ResourceLocation(DankStorage.MODID,
          "textures/container/gui/dank1.png");

  private static final ResourceLocation background2 = new ResourceLocation(DankStorage.MODID,
          "textures/container/gui/dank2.png");

  private static final ResourceLocation background3 = new ResourceLocation(DankStorage.MODID,
          "textures/container/gui/dank3.png");

  private static final ResourceLocation background4 = new ResourceLocation(DankStorage.MODID,
          "textures/container/gui/dank4.png");

  private static final ResourceLocation background5 = new ResourceLocation(DankStorage.MODID,
          "textures/container/gui/dank5.png");

  private static final ResourceLocation background6 = new ResourceLocation("textures/gui/container/generic_54.png");

  private static final ResourceLocation background7 = new ResourceLocation(DankStorage.MODID,
          "textures/container/gui/dank7.png");

  public static class DankStorageScreen1 extends AbstractTileDankStorageScreen<DankContainers.TileDankContainer1> {

    public DankStorageScreen1(DankContainers.TileDankContainer1 container, PlayerInventory playerinventory, ITextComponent component) {
      super(container, playerinventory, component, background1);
    }
  }

  public static class DankStorageScreen2 extends AbstractTileDankStorageScreen<DankContainers.TileDankContainer2> {

    public DankStorageScreen2(DankContainers.TileDankContainer2 container, PlayerInventory playerinventory, ITextComponent component) {
      super(container, playerinventory, component, background2);
    }
  }

  public static class DankStorageScreen3 extends AbstractTileDankStorageScreen<DankContainers.TileDankContainer3> {

    public DankStorageScreen3(DankContainers.TileDankContainer3 container, PlayerInventory playerinventory, ITextComponent component) {
      super(container, playerinventory, component, background3);
    }
  }

  public static class DankStorageScreen4 extends AbstractTileDankStorageScreen<DankContainers.TileDankContainer4> {

    public DankStorageScreen4(DankContainers.TileDankContainer4 container, PlayerInventory playerinventory, ITextComponent component) {
      super(container, playerinventory, component, background4);
    }
  }

  public static class DankStorageScreen5 extends AbstractTileDankStorageScreen<DankContainers.TileDankContainer5> {

    public DankStorageScreen5(DankContainers.TileDankContainer5 container, PlayerInventory playerinventory, ITextComponent component) {
      super(container, playerinventory, component, background5);
    }
  }

  public static class DankStorageScreen6 extends AbstractTileDankStorageScreen<DankContainers.TileDankContainer6> {

    public DankStorageScreen6(DankContainers.TileDankContainer6 container, PlayerInventory playerinventory, ITextComponent component) {
      super(container, playerinventory, component, background6);
    }
  }

  public static class DankStorageScreen7 extends AbstractTileDankStorageScreen<DankContainers.TileDankContainer7> {

    public DankStorageScreen7(DankContainers.TileDankContainer7 container, PlayerInventory playerinventory, ITextComponent component) {
      super(container, playerinventory, component, background7);
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
  }
}