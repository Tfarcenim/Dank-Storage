package tfar.dankstorage.client.screens;

import tfar.dankstorage.container.AbstractTileDankContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class DockScreen extends AbstractAbstractDankStorageScreen<AbstractTileDankContainer> {

  public DockScreen(AbstractTileDankContainer container, PlayerInventory playerinventory, ITextComponent component, ResourceLocation background) {
    super(container,playerinventory, component,background);
  }

  @Override
  public ITextComponent getContainerName() {
    return container.te.getDisplayName();
  }

  public static DockScreen t1(AbstractTileDankContainer container, PlayerInventory playerinventory, ITextComponent component) {
    return new DockScreen(container,playerinventory,component,DankScreens.background1);
  }

  public static DockScreen t2(AbstractTileDankContainer container,PlayerInventory playerinventory, ITextComponent component) {
    return new DockScreen(container,playerinventory,component,DankScreens.background2);
  }

  public static DockScreen t3(AbstractTileDankContainer container,PlayerInventory playerinventory, ITextComponent component) {
    return new DockScreen(container,playerinventory,component,DankScreens.background3);
  }

  public static DockScreen t4(AbstractTileDankContainer container,PlayerInventory playerinventory, ITextComponent component) {
    return new DockScreen(container,playerinventory,component,DankScreens.background4);
  }

  public static DockScreen t5(AbstractTileDankContainer container,PlayerInventory playerinventory, ITextComponent component) {
    return new DockScreen(container,playerinventory,component,DankScreens.background5);
  }

  public static DockScreen t6(AbstractTileDankContainer container,PlayerInventory playerinventory, ITextComponent component) {
    return new DockScreen(container,playerinventory,component,DankScreens.background6);
  }

  public static DockScreen t7(AbstractTileDankContainer container,PlayerInventory playerinventory, ITextComponent component) {
    return new DockScreen(container,playerinventory,component,DankScreens.background7);
  }

}