package tfar.dankstorage.container;

import tfar.dankstorage.DankStorage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DankContainers {

  public static class PortableDankContainer1 extends AbstractPortableDankContainer {

    public PortableDankContainer1(int p_i50105_2_, PlayerInventory playerInventory, PlayerEntity player) {
      super(DankStorage.Objects.portable_dank_1_container, p_i50105_2_,playerInventory,player, 1);
    }
  }

  public static class PortableDankContainer2 extends AbstractPortableDankContainer {

    public PortableDankContainer2(int p_i50105_2_, PlayerInventory playerInventory, PlayerEntity player) {
      super(DankStorage.Objects.portable_dank_2_container, p_i50105_2_,playerInventory,player, 2);
    }
  }

  public static class PortableDankContainer3 extends AbstractPortableDankContainer {

    public PortableDankContainer3(int p_i50105_2_, PlayerInventory playerInventory, PlayerEntity player) {
      super(DankStorage.Objects.portable_dank_3_container, p_i50105_2_,playerInventory,player, 3);
    }
  }

  public static class PortableDankContainer4 extends AbstractPortableDankContainer {

    public PortableDankContainer4(int p_i50105_2_, PlayerInventory playerInventory, PlayerEntity player) {
      super(DankStorage.Objects.portable_dank_4_container, p_i50105_2_,playerInventory,player, 4);
    }
  }

  public static class PortableDankContainer5 extends AbstractPortableDankContainer {

    public PortableDankContainer5(int p_i50105_2_, PlayerInventory playerInventory, PlayerEntity player) {
      super(DankStorage.Objects.portable_dank_5_container, p_i50105_2_,playerInventory,player, 5);
    }
  }

  public static class PortableDankContainer6 extends AbstractPortableDankContainer {

    public PortableDankContainer6(int p_i50105_2_, PlayerInventory playerInventory, PlayerEntity player) {
      super(DankStorage.Objects.portable_dank_6_container, p_i50105_2_,playerInventory,player, 6);
    }
  }

  public static class PortableDankContainer7 extends AbstractPortableDankContainer {

    public PortableDankContainer7(int p_i50105_2_, PlayerInventory playerInventory, PlayerEntity player) {
      super(DankStorage.Objects.portable_dank_7_container, p_i50105_2_,playerInventory,player, 9);
    }
  }

  public static class TileDankContainer1 extends AbstractTileDankContainer {

    public TileDankContainer1(int p_i50105_2_, World world, BlockPos pos, PlayerInventory playerInventory, PlayerEntity player) {
      super(DankStorage.Objects.dank_1_container, p_i50105_2_,world,pos,playerInventory,player, 1);
    }
  }

  public static class TileDankContainer2 extends AbstractTileDankContainer {

    public TileDankContainer2(int p_i50105_2_, World world, BlockPos pos, PlayerInventory playerInventory, PlayerEntity player) {
      super(DankStorage.Objects.dank_2_container, p_i50105_2_,world,pos,playerInventory,player, 2);
    }
  }

  public static class TileDankContainer3 extends AbstractTileDankContainer {

    public TileDankContainer3(int p_i50105_2_, World world, BlockPos pos, PlayerInventory playerInventory, PlayerEntity player) {
      super(DankStorage.Objects.dank_3_container, p_i50105_2_,world,pos,playerInventory,player, 3);
    }
  }

  public static class TileDankContainer4 extends AbstractTileDankContainer {

    public TileDankContainer4(int p_i50105_2_, World world, BlockPos pos, PlayerInventory playerInventory, PlayerEntity player) {
      super(DankStorage.Objects.dank_4_container, p_i50105_2_,world,pos,playerInventory,player, 4);
    }
  }

  public static class TileDankContainer5 extends AbstractTileDankContainer {

    public TileDankContainer5(int p_i50105_2_, World world, BlockPos pos, PlayerInventory playerInventory, PlayerEntity player) {
      super(DankStorage.Objects.dank_5_container, p_i50105_2_,world,pos,playerInventory,player, 5);
    }
  }

  public static class TileDankContainer6 extends AbstractTileDankContainer {

    public TileDankContainer6(int p_i50105_2_, World world, BlockPos pos, PlayerInventory playerInventory, PlayerEntity player) {
      super(DankStorage.Objects.dank_6_container, p_i50105_2_,world,pos,playerInventory,player, 6);
    }
  }

  public static class TileDankContainer7 extends AbstractTileDankContainer {

    public TileDankContainer7(int p_i50105_2_, World world, BlockPos pos, PlayerInventory playerInventory, PlayerEntity player) {
      super(DankStorage.Objects.dank_7_container, p_i50105_2_,world,pos,playerInventory,player, 9);
    }
  }
}

