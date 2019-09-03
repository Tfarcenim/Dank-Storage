package com.tfar.dankstorage;

import com.tfar.dankstorage.block.DankItemBlock;
import com.tfar.dankstorage.container.DankContainers;
import com.tfar.dankstorage.inventory.DankHandler;
import com.tfar.dankstorage.inventory.PortableDankHandler;
import com.tfar.dankstorage.screen.DankScreens;
import com.tfar.dankstorage.util.DankConstants;
import com.tfar.dankstorage.util.Utils;
import com.tfar.dankstorage.tile.AbstractDankStorageTile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import javax.annotation.Nullable;

public class GuiHandler implements IGuiHandler {
  /**
   * Returns a Server side Container to be displayed to the user.
   *
   * @param ID     The Gui ID Number
   * @param player The player viewing the Gui
   * @param world  The current world
   * @param x      X Position
   * @param y      Y Position
   * @param z      Z Position
   * @return A GuiScreen/Container to be displayed to the user, null if none.
   */
  @Nullable
  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    BlockPos pos = new BlockPos(x, y, z);
    TileEntity te = world.getTileEntity(pos);
    if (ID == DankConstants.TILE_GUI_ID && te instanceof AbstractDankStorageTile) {
      DankHandler handler = ((AbstractDankStorageTile) te).itemHandler;
      int tier = Utils.getTier(((AbstractDankStorageTile) te).getDank().getRegistryName());
      switch (tier) {
        case 1:
          return new DankContainers.DankContainer1(world, pos, player.inventory, player, handler);
        case 2:
          return new DankContainers.DankContainer2(world, pos, player.inventory, player, handler);
        case 3:
          return new DankContainers.DankContainer3(world, pos, player.inventory, player, handler);
        case 4:
          return new DankContainers.DankContainer4(world, pos, player.inventory, player, handler);
        case 5:
          return new DankContainers.DankContainer5(world, pos, player.inventory, player, handler);
        case 6:
          return new DankContainers.DankContainer6(world, pos, player.inventory, player, handler);
        case 7:
          return new DankContainers.DankContainer7(world, pos, player.inventory, player, handler);
      }
    } else if (ID == DankConstants.BAG_GUI_ID && player.getHeldItemMainhand().getItem() instanceof DankItemBlock) {
      ItemStack bag = player.getHeldItemMainhand();
      int tier = Utils.getTier(bag);
      PortableDankHandler handler = Utils.getHandler(bag);
      switch (tier) {
        case 1:
          return new DankContainers.PortableDankContainer1(player.inventory, player, handler);
        case 2:
          return new DankContainers.PortableDankContainer2(player.inventory, player, handler);
        case 3:
          return new DankContainers.PortableDankContainer3(player.inventory, player, handler);
        case 4:
          return new DankContainers.PortableDankContainer4(player.inventory, player, handler);
        case 5:
          return new DankContainers.PortableDankContainer5(player.inventory, player, handler);
        case 6:
          return new DankContainers.PortableDankContainer6(player.inventory, player, handler);
        case 7:
          return new DankContainers.PortableDankContainer7(player.inventory, player, handler);
      }
    }
    return null;
  }

  /**
   * Returns a Container to be displayed to the user. On the client side, this
   * needs to return a instance of GuiScreen On the server side, this needs to
   * return a instance of Container
   *
   * @param ID     The Gui ID Number
   * @param player The player viewing the Gui
   * @param world  The current world
   * @param x      X Position
   * @param y      Y Position
   * @param z      Z Position
   * @return A GuiScreen/Container to be displayed to the user, null if none.
   */
  @Nullable
  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    BlockPos pos = new BlockPos(x, y, z);
    TileEntity te = world.getTileEntity(pos);
    if (ID == DankConstants.TILE_GUI_ID && te instanceof AbstractDankStorageTile) {
      DankHandler handler = ((AbstractDankStorageTile) te).itemHandler;
      int tier = Utils.getTier(((AbstractDankStorageTile) te).getDank().getRegistryName());
      switch (tier) {
        case 1:
          return new DankScreens.DankStorageScreen1(new DankContainers.DankContainer1(world, pos, player.inventory, player, handler), player.inventory);
        case 2:
          return new DankScreens.DankStorageScreen2(new DankContainers.DankContainer2(world, pos, player.inventory, player, handler), player.inventory);
        case 3:
          return new DankScreens.DankStorageScreen3(new DankContainers.DankContainer3(world, pos, player.inventory, player, handler), player.inventory);
        case 4:
          return new DankScreens.DankStorageScreen4(new DankContainers.DankContainer4(world, pos, player.inventory, player, handler), player.inventory);
        case 5:
          return new DankScreens.DankStorageScreen5(new DankContainers.DankContainer5(world, pos, player.inventory, player, handler), player.inventory);
        case 6:
          return new DankScreens.DankStorageScreen6(new DankContainers.DankContainer6(world, pos, player.inventory, player, handler), player.inventory);
        case 7:
          return new DankScreens.DankStorageScreen7(new DankContainers.DankContainer7(world, pos, player.inventory, player, handler), player.inventory);
      }
    } else if (ID == DankConstants.BAG_GUI_ID && player.getHeldItemMainhand().getItem() instanceof DankItemBlock) {
      ItemStack bag = player.getHeldItemMainhand();
      int tier = Utils.getTier(bag);
      PortableDankHandler handler = Utils.getHandler(bag);
      switch (tier) {
        case 1:
          return new DankScreens.PortableDankStorageScreen1(new DankContainers.PortableDankContainer1(player.inventory, player, handler), player.inventory);
        case 2:
          return new DankScreens.PortableDankStorageScreen2(new DankContainers.PortableDankContainer2(player.inventory, player, handler), player.inventory);
        case 3:
          return new DankScreens.PortableDankStorageScreen3(new DankContainers.PortableDankContainer3(player.inventory, player, handler), player.inventory);
        case 4:
          return new DankScreens.PortableDankStorageScreen4(new DankContainers.PortableDankContainer4(player.inventory, player, handler), player.inventory);
        case 5:
          return new DankScreens.PortableDankStorageScreen5(new DankContainers.PortableDankContainer5(player.inventory, player, handler), player.inventory);
        case 6:
          return new DankScreens.PortableDankStorageScreen6(new DankContainers.PortableDankContainer6(player.inventory, player, handler), player.inventory);
        case 7:
          return new DankScreens.PortableDankStorageScreen7(new DankContainers.PortableDankContainer7(player.inventory, player, handler), player.inventory);
      }
    }
    return null;
  }
}
