package com.tfar.dankstorage.container;

import com.tfar.dankstorage.DankStorage;
import com.tfar.dankstorage.inventory.DankHandler;
import com.tfar.dankstorage.inventory.PortableDankHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DankContainers {

  public static class PortableDankContainer1 extends AbstractPortableDankContainer {

    public PortableDankContainer1(int p_i50105_2_, PlayerInventory playerInventory, PlayerEntity player, PortableDankHandler handler) {
      super(DankStorage.Objects.portable_dank_1_container, p_i50105_2_,playerInventory,player, handler,1);
    }
  }

  public static class PortableDankContainer2 extends AbstractPortableDankContainer {

    public PortableDankContainer2(int p_i50105_2_, PlayerInventory playerInventory, PlayerEntity player, PortableDankHandler handler) {
      super(DankStorage.Objects.portable_dank_2_container, p_i50105_2_,playerInventory,player, handler,2);
    }
  }

  public static class PortableDankContainer3 extends AbstractPortableDankContainer {

    public PortableDankContainer3(int p_i50105_2_, PlayerInventory playerInventory, PlayerEntity player, PortableDankHandler handler) {
      super(DankStorage.Objects.portable_dank_3_container, p_i50105_2_,playerInventory,player, handler,3);
    }
  }

  public static class PortableDankContainer4 extends AbstractPortableDankContainer {

    public PortableDankContainer4(int p_i50105_2_, PlayerInventory playerInventory, PlayerEntity player, PortableDankHandler handler) {
      super(DankStorage.Objects.portable_dank_4_container, p_i50105_2_,playerInventory,player, handler,4);
    }
  }

  public static class PortableDankContainer5 extends AbstractPortableDankContainer {

    public PortableDankContainer5(int p_i50105_2_, PlayerInventory playerInventory, PlayerEntity player, PortableDankHandler handler) {
      super(DankStorage.Objects.portable_dank_5_container, p_i50105_2_,playerInventory,player, handler,5);
    }
  }

  public static class PortableDankContainer6 extends AbstractPortableDankContainer {

    public PortableDankContainer6(int p_i50105_2_, PlayerInventory playerInventory, PlayerEntity player, PortableDankHandler handler) {
      super(DankStorage.Objects.portable_dank_6_container, p_i50105_2_,playerInventory,player, handler,6);
    }
  }

  public static class PortableDankContainer7 extends AbstractPortableDankContainer {

    public PortableDankContainer7(int p_i50105_2_, PlayerInventory playerInventory, PlayerEntity player, PortableDankHandler handler) {
      super(DankStorage.Objects.portable_dank_7_container, p_i50105_2_,playerInventory,player, handler,9);
    }
  }

  public static class DankContainer1 extends AbstractDankContainer {

    public DankContainer1(int p_i50105_2_, World world, BlockPos pos, PlayerInventory playerInventory, PlayerEntity player, DankHandler handler) {
      super(DankStorage.Objects.dank_1_container, p_i50105_2_,world,pos,playerInventory,player,handler,1);
    }
  }

  public static class DankContainer2 extends AbstractDankContainer {

    public DankContainer2(int p_i50105_2_, World world, BlockPos pos, PlayerInventory playerInventory, PlayerEntity player, DankHandler handler) {
      super(DankStorage.Objects.dank_2_container, p_i50105_2_,world,pos,playerInventory,player,handler,2);
    }
  }

  public static class DankContainer3 extends AbstractDankContainer {

    public DankContainer3(int p_i50105_2_, World world, BlockPos pos, PlayerInventory playerInventory, PlayerEntity player, DankHandler handler) {
      super(DankStorage.Objects.dank_3_container, p_i50105_2_,world,pos,playerInventory,player,handler,3);
    }
  }

  public static class DankContainer4 extends AbstractDankContainer {

    public DankContainer4(int p_i50105_2_, World world, BlockPos pos, PlayerInventory playerInventory, PlayerEntity player, DankHandler handler) {
      super(DankStorage.Objects.dank_4_container, p_i50105_2_,world,pos,playerInventory,player,handler,4);
    }
  }

  public static class DankContainer5 extends AbstractDankContainer {

    public DankContainer5(int p_i50105_2_, World world, BlockPos pos, PlayerInventory playerInventory, PlayerEntity player, DankHandler handler) {
      super(DankStorage.Objects.dank_5_container, p_i50105_2_,world,pos,playerInventory,player,handler,5);
    }
  }

  public static class DankContainer6 extends AbstractDankContainer {

    public DankContainer6(int p_i50105_2_, World world, BlockPos pos, PlayerInventory playerInventory, PlayerEntity player, DankHandler handler) {
      super(DankStorage.Objects.dank_6_container, p_i50105_2_,world,pos,playerInventory,player,handler,6);
    }
  }

  public static class DankContainer7 extends AbstractDankContainer {

    public DankContainer7(int p_i50105_2_, World world, BlockPos pos, PlayerInventory playerInventory, PlayerEntity player, DankHandler handler) {
      super(DankStorage.Objects.dank_7_container, p_i50105_2_,world,pos,playerInventory,player,handler,9);
    }
  }
}

