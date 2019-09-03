package com.tfar.dankstorage.tile;

import com.tfar.dankstorage.DankStorage;
import com.tfar.dankstorage.container.DankContainers;
import com.tfar.dankstorage.inventory.DankHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

import javax.annotation.Nullable;

public class DankTiles {
  public static class DankStorageTile1 extends AbstractDankStorageTile {

    public DankStorageTile1() {
      super(1,256);
    }

  //  @Override
  //  public ITextComponent getName() {
  //    return this.hasCustomName() ? new TextComponentTranslation(this.customName) : new TextComponentTranslation("container.dankstorage.dank_1");
  //  }

    @Override
    public Item getDank() {
      return Item.getItemFromBlock(DankStorage.Objects.dank_1);
    }
  }

  public static class DankStorageTile2 extends AbstractDankStorageTile {

    public DankStorageTile2() {
      super(2,1024);
    }

    /*@Nullable
    @Override
    public Container createMenu(int i, InventoryPlayer playerInventory, EntityPlayer playerEntity) {
      AbstractDankStorageTile tile = (AbstractDankStorageTile) world.getTileEntity(pos);
      DankHandler handler = tile.itemHandler;
      return new DankContainers.DankContainer2(i,world,pos,playerInventory,playerEntity,handler);
    }*/

  //  @Override
  //  public ITextComponent getName() {
  //    return this.hasCustomName() ? new TextComponentTranslation(this.customName) : new TextComponentTranslation("container.dankstorage.dank_2");
  //  }

    @Override
    public Item getDank() {
      return Item.getItemFromBlock(DankStorage.Objects.dank_2);
    }
  }

  public static class DankStorageTile3 extends AbstractDankStorageTile {

    public DankStorageTile3() {
      super(3,4096);
    }


    @Override
    public Item getDank() {
      return Item.getItemFromBlock(DankStorage.Objects.dank_3);
    }
  }

  public static class DankStorageTile4 extends AbstractDankStorageTile {

    public DankStorageTile4() {
      super(4,16384);
    }

    @Override
    public Item getDank() {
      return Item.getItemFromBlock(DankStorage.Objects.dank_4);
    }
  }

  public static class DankStorageTile5 extends AbstractDankStorageTile {

    public DankStorageTile5() {
      super(5,65536);
    }

    @Override
    public Item getDank() {
      return Item.getItemFromBlock(DankStorage.Objects.dank_5);
    }
  }

  public static class DankStorageTile6 extends AbstractDankStorageTile {

    public DankStorageTile6() {
      super(6,262144);
    }

    @Override
    public Item getDank() {
      return Item.getItemFromBlock(DankStorage.Objects.dank_6);
    }
  }

  public static class DankStorageTile7 extends AbstractDankStorageTile {

    public DankStorageTile7() {
      super(9,Integer.MAX_VALUE);
    }


    @Override
    public Item getDank() {
      return Item.getItemFromBlock(DankStorage.Objects.dank_7);
    }
  }
}
