package tfar.dankstorage.container;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import tfar.dankstorage.DankStorage;
import tfar.dankstorage.inventory.CappedSlot;
import tfar.dankstorage.inventory.DankHandler;
import tfar.dankstorage.inventory.LockedSlot;
import tfar.dankstorage.inventory.PortableDankHandler;
import tfar.dankstorage.utils.DankMenuType;
import tfar.dankstorage.utils.Utils;

public class PortableDankContainer extends AbstractDankContainer {


  //client
  public PortableDankContainer(ContainerType<?> type, int id, PlayerInventory playerInventory) {
    this(type, id,playerInventory,new DankHandler(((DankMenuType<?>)type).stats),new IntArray(((DankMenuType<?>)type).stats.slots + 2));
  }

  //common
  public PortableDankContainer(ContainerType<?> type, int id, PlayerInventory playerInventory, DankHandler dankHandler, IIntArray propertyDelegate) {
    super(type, id,playerInventory,dankHandler,propertyDelegate);
    addOwnSlots(true);
    addPlayerSlots(playerInventory, playerInventory.currentItem);
    if (!playerInventory.player.world.isRemote) {
      propertyDelegate.set(dankHandler.getSlots(), Utils.getNbtSize(((PortableDankHandler) dankHandler).bag));
    }
  }

  protected void addPlayerSlots(PlayerInventory playerinventory, int locked) {
    int yStart = 32 + 18 * rows;
    for (int row = 0; row < 3; ++row) {
      for (int col = 0; col < 9; ++col) {
        int x = 8 + col * 18;
        int y = row * 18 + yStart;
        this.addSlot(new CappedSlot(playerinventory, col + row * 9 + 9, x, y));
      }
    }

    for (int row = 0; row < 9; ++row) {
      int x = 8 + row * 18;
      int y = yStart + 58;
      if (row != locked)
      this.addSlot(new CappedSlot(playerinventory, row, x, y));
      else this.addSlot(new LockedSlot(playerinventory, row, x, y));
    }
  }

  public static PortableDankContainer dank1c(int id, PlayerInventory playerInventory) {
    return new PortableDankContainer(DankStorage.Objects.portable_dank_1_container, id,playerInventory);
  }

  public static PortableDankContainer dank2c(int id, PlayerInventory playerInventory) {
    return new PortableDankContainer(DankStorage.Objects.portable_dank_2_container, id,playerInventory);
  }

  public static PortableDankContainer dank3c(int id, PlayerInventory playerInventory) {
    return new PortableDankContainer(DankStorage.Objects.portable_dank_3_container, id,playerInventory);
  }

  public static PortableDankContainer dank4c(int id, PlayerInventory playerInventory) {
    return new PortableDankContainer(DankStorage.Objects.portable_dank_4_container, id, playerInventory);
  }

  public static PortableDankContainer dank5c(int id, PlayerInventory playerInventory) {
    return new PortableDankContainer(DankStorage.Objects.portable_dank_5_container, id,playerInventory);
  }

  public static PortableDankContainer dank6c(int id, PlayerInventory playerInventory) {
    return new PortableDankContainer(DankStorage.Objects.portable_dank_6_container, id,playerInventory);
  }

  public static PortableDankContainer dank7c(int id, PlayerInventory playerInventory) {
    return new PortableDankContainer(DankStorage.Objects.portable_dank_7_container, id,playerInventory);
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  public static PortableDankContainer dank1s(int id, PlayerInventory playerInventory, PortableDankHandler handler, IIntArray iIntArray) {
    return new PortableDankContainer(DankStorage.Objects.portable_dank_1_container, id,playerInventory,handler,iIntArray);
  }

  public static PortableDankContainer dank2s(int id, PlayerInventory playerInventory, PortableDankHandler handler, IIntArray iIntArray) {
    return new PortableDankContainer(DankStorage.Objects.portable_dank_2_container, id, playerInventory, handler, iIntArray);
  }

  public static PortableDankContainer dank3s(int id, PlayerInventory playerInventory, PortableDankHandler handler, IIntArray iIntArray) {
    return new PortableDankContainer(DankStorage.Objects.portable_dank_3_container, id,playerInventory,handler,iIntArray);
  }

  public static PortableDankContainer dank4s(int id, PlayerInventory playerInventory, PortableDankHandler handler, IIntArray iIntArray) {
    return new PortableDankContainer(DankStorage.Objects.portable_dank_4_container, id,playerInventory,handler,iIntArray);
  }

  public static PortableDankContainer dank5s(int id, PlayerInventory playerInventory, PortableDankHandler handler, IIntArray iIntArray) {
    return new PortableDankContainer(DankStorage.Objects.portable_dank_5_container, id,playerInventory,handler,iIntArray);
  }

  public static PortableDankContainer dank6s(int id, PlayerInventory playerInventory, PortableDankHandler handler, IIntArray iIntArray) {
    return new PortableDankContainer(DankStorage.Objects.portable_dank_6_container, id,playerInventory,handler,iIntArray);
  }

  public static PortableDankContainer dank7s(int id, PlayerInventory playerInventory, PortableDankHandler handler, IIntArray iIntArray) {
    return new PortableDankContainer(DankStorage.Objects.portable_dank_7_container, id,playerInventory,handler,iIntArray);
  }
}

