package tfar.dankstorage.client.screens;

import tfar.dankstorage.DankStorage;
import tfar.dankstorage.container.AbstractTileDankContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class DockScreen extends AbstractDankStorageScreen<AbstractTileDankContainer> {

  public DockScreen(AbstractTileDankContainer container, PlayerInventory playerinventory, ITextComponent component, ResourceLocation background) {
    super(container,playerinventory, component,background);
  }

  static final ResourceLocation background1 = new ResourceLocation(DankStorage.MODID,
          "textures/container/gui/dank1.png");

  static final ResourceLocation background2 = new ResourceLocation(DankStorage.MODID,
          "textures/container/gui/dank2.png");

  static final ResourceLocation background3 = new ResourceLocation(DankStorage.MODID,
          "textures/container/gui/dank3.png");

  static final ResourceLocation background4 = new ResourceLocation(DankStorage.MODID,
          "textures/container/gui/dank4.png");

  static final ResourceLocation background5 = new ResourceLocation(DankStorage.MODID,
          "textures/container/gui/dank5.png");

  static final ResourceLocation background6 = new ResourceLocation("textures/gui/container/generic_54.png");

  static final ResourceLocation background7 = new ResourceLocation(DankStorage.MODID,
          "textures/container/gui/dank7.png");

  public static DockScreen t1(AbstractTileDankContainer container, PlayerInventory playerinventory, ITextComponent component) {
    return new DockScreen(container,playerinventory,component,background1);
  }

  public static DockScreen t2(AbstractTileDankContainer container,PlayerInventory playerinventory, ITextComponent component) {
    return new DockScreen(container,playerinventory,component,background2);
  }

  public static DockScreen t3(AbstractTileDankContainer container,PlayerInventory playerinventory, ITextComponent component) {
    return new DockScreen(container,playerinventory,component,background3);
  }

  public static DockScreen t4(AbstractTileDankContainer container,PlayerInventory playerinventory, ITextComponent component) {
    return new DockScreen(container,playerinventory,component,background4);
  }

  public static DockScreen t5(AbstractTileDankContainer container,PlayerInventory playerinventory, ITextComponent component) {
    return new DockScreen(container,playerinventory,component,background5);
  }

  public static DockScreen t6(AbstractTileDankContainer container,PlayerInventory playerinventory, ITextComponent component) {
    return new DockScreen(container,playerinventory,component,background6);
  }

  public static DockScreen t7(AbstractTileDankContainer container,PlayerInventory playerinventory, ITextComponent component) {
    return new DockScreen(container,playerinventory,component,background7);
  }

}