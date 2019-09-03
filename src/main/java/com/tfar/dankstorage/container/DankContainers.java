package com.tfar.dankstorage.container;

import com.tfar.dankstorage.inventory.DankHandler;
import com.tfar.dankstorage.inventory.PortableDankHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DankContainers {

  public static class PortableDankContainer1 extends AbstractPortableDankContainer {

    public PortableDankContainer1(InventoryPlayer playerInventory, EntityPlayer player, PortableDankHandler handler) {
      super(playerInventory,player, handler,1);
    }
  }

  public static class PortableDankContainer2 extends AbstractPortableDankContainer {

    public PortableDankContainer2(InventoryPlayer playerInventory, EntityPlayer player, PortableDankHandler handler) {
      super(playerInventory,player, handler,2);
    }
  }

  public static class PortableDankContainer3 extends AbstractPortableDankContainer {

    public PortableDankContainer3(InventoryPlayer playerInventory, EntityPlayer player, PortableDankHandler handler) {
      super(playerInventory,player, handler,3);
    }
  }

  public static class PortableDankContainer4 extends AbstractPortableDankContainer {

    public PortableDankContainer4(InventoryPlayer playerInventory, EntityPlayer player, PortableDankHandler handler) {
      super(playerInventory,player, handler,4);
    }
  }

  public static class PortableDankContainer5 extends AbstractPortableDankContainer {

    public PortableDankContainer5(InventoryPlayer playerInventory, EntityPlayer player, PortableDankHandler handler) {
      super(playerInventory,player, handler,5);
    }
  }

  public static class PortableDankContainer6 extends AbstractPortableDankContainer {

    public PortableDankContainer6(InventoryPlayer playerInventory, EntityPlayer player, PortableDankHandler handler) {
      super(playerInventory,player, handler,6);
    }
  }

  public static class PortableDankContainer7 extends AbstractPortableDankContainer {

    public PortableDankContainer7(InventoryPlayer playerInventory, EntityPlayer player, PortableDankHandler handler) {
      super(playerInventory,player, handler,9);
    }
  }

  public static class DankContainer1 extends AbstractDankContainer {

    public DankContainer1(World world, BlockPos pos, InventoryPlayer playerInventory, EntityPlayer player, DankHandler handler) {
      super(world,pos,playerInventory,player,handler,1);
    }
  }

  public static class DankContainer2 extends AbstractDankContainer {

    public DankContainer2(World world,BlockPos pos, InventoryPlayer playerInventory, EntityPlayer player, DankHandler handler) {
      super(world,pos,playerInventory,player,handler,2);
    }
  }

  public static class DankContainer3 extends AbstractDankContainer {

    public DankContainer3(World world, BlockPos pos, InventoryPlayer playerInventory, EntityPlayer player, DankHandler handler) {
      super(world,pos,playerInventory,player,handler,3);
    }
  }

  public static class DankContainer4 extends AbstractDankContainer {

    public DankContainer4(World world, BlockPos pos, InventoryPlayer playerInventory, EntityPlayer player, DankHandler handler) {
      super(world,pos,playerInventory,player,handler,4);
    }
  }

  public static class DankContainer5 extends AbstractDankContainer {

    public DankContainer5(World world, BlockPos pos, InventoryPlayer playerInventory, EntityPlayer player, DankHandler handler) {
      super(world,pos,playerInventory,player,handler,5);
    }
  }

  public static class DankContainer6 extends AbstractDankContainer {

    public DankContainer6(World world,BlockPos pos, InventoryPlayer playerInventory, EntityPlayer player, DankHandler handler) {
      super(world,pos,playerInventory,player,handler,6);
    }
  }

  public static class DankContainer7 extends AbstractDankContainer {

    public DankContainer7(World world, BlockPos pos, InventoryPlayer playerInventory, EntityPlayer player, DankHandler handler) {
      super(world,pos,playerInventory,player,handler,9);
    }
  }
}

