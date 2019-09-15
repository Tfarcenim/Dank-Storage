package com.tfar.dankstorage.tile;

import com.tfar.dankstorage.DankStorage;
import com.tfar.dankstorage.container.DankContainers;
import com.tfar.dankstorage.inventory.DankHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.Item;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;

public class DankTiles {
  public static class DankStorageTile1 extends AbstractDankStorageTile {

    public DankStorageTile1() {
      super(DankStorage.Objects.dank_1_tile,1,256);
    }

    @Nullable
    @Override
    public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
      AbstractDankStorageTile tile = (AbstractDankStorageTile) world.getTileEntity(pos);
      DankHandler handler = tile.itemHandler;
      return new DankContainers.DankContainer1(i,world,pos,playerInventory,playerEntity,handler);
    }
    @Override
    public ITextComponent getName() {
      return this.hasCustomName() ? new TranslationTextComponent(this.customName) : new TranslationTextComponent("container.dankstorage.dank_1");
    }

    @Override
    public Item getDank() {
      return DankStorage.Objects.dank_1.asItem();
    }
  }

  public static class DankStorageTile2 extends AbstractDankStorageTile {

    public DankStorageTile2() {
      super(DankStorage.Objects.dank_2_tile,2,1024);
    }

    @Nullable
    @Override
    public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
      AbstractDankStorageTile tile = (AbstractDankStorageTile) world.getTileEntity(pos);
      DankHandler handler = tile.itemHandler;
      return new DankContainers.DankContainer2(i,world,pos,playerInventory,playerEntity,handler);
    }

    @Override
    public ITextComponent getName() {
      return this.hasCustomName() ? new TranslationTextComponent(this.customName) : new TranslationTextComponent("container.dankstorage.dank_2");
    }

    @Override
    public Item getDank() {
      return DankStorage.Objects.dank_2.asItem();
    }
  }

  public static class DankStorageTile3 extends AbstractDankStorageTile {

    public DankStorageTile3() {
      super(DankStorage.Objects.dank_3_tile,3,4096);
    }

    @Nullable
    @Override
    public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
      AbstractDankStorageTile tile = (AbstractDankStorageTile) world.getTileEntity(pos);
      DankHandler handler = tile.itemHandler;
      return new DankContainers.DankContainer3(i,world,pos,playerInventory,playerEntity,handler);
    }

    @Override
    public ITextComponent getName() {
      return this.hasCustomName() ? new TranslationTextComponent(this.customName) : new TranslationTextComponent("container.dankstorage.dank_3");
    }

    @Override
    public Item getDank() {
      return DankStorage.Objects.dank_3.asItem();
    }
  }

  public static class DankStorageTile4 extends AbstractDankStorageTile {

    public DankStorageTile4() {
      super(DankStorage.Objects.dank_4_tile,4,16384);
    }

    @Nullable
    @Override
    public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
      AbstractDankStorageTile tile = (AbstractDankStorageTile) world.getTileEntity(pos);
      DankHandler handler = tile.itemHandler;
      return new DankContainers.DankContainer4(i,world,pos,playerInventory,playerEntity,handler);
    }

    @Override
    public ITextComponent getName() {
      return this.hasCustomName() ? new TranslationTextComponent(this.customName) : new TranslationTextComponent("container.dankstorage.dank_4");
    }

    @Override
    public Item getDank() {
      return DankStorage.Objects.dank_4.asItem();
    }
  }

  public static class DankStorageTile5 extends AbstractDankStorageTile {

    public DankStorageTile5() {
      super(DankStorage.Objects.dank_5_tile,5,65536);
    }

    @Nullable
    @Override
    public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
      AbstractDankStorageTile tile = (AbstractDankStorageTile) world.getTileEntity(pos);
      DankHandler handler = tile.itemHandler;
      return new DankContainers.DankContainer5(i,world,pos,playerInventory,playerEntity,handler);
    }

    @Override
    public ITextComponent getName() {
      return this.hasCustomName() ? new TranslationTextComponent(this.customName) : new TranslationTextComponent("container.dankstorage.dank_5");
    }

    @Override
    public Item getDank() {
      return DankStorage.Objects.dank_5.asItem();
    }
  }

  public static class DankStorageTile6 extends AbstractDankStorageTile {

    public DankStorageTile6() {
      super(DankStorage.Objects.dank_6_tile,6,262144);
    }

    @Nullable
    @Override
    public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
      AbstractDankStorageTile tile = (AbstractDankStorageTile) world.getTileEntity(pos);
      DankHandler handler = tile.itemHandler;
      return new DankContainers.DankContainer6(i,world,pos,playerInventory,playerEntity,handler);
    }

    @Override
    public ITextComponent getName() {
      return this.hasCustomName() ? new TranslationTextComponent(this.customName) : new TranslationTextComponent("container.dankstorage.dank_6");
    }

    @Override
    public Item getDank() {
      return DankStorage.Objects.dank_6.asItem();
    }
  }

  public static class DankStorageTile7 extends AbstractDankStorageTile {

    public DankStorageTile7() {
      super(DankStorage.Objects.dank_7_tile,9,Integer.MAX_VALUE);
    }

    @Nullable
    @Override
    public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
      AbstractDankStorageTile tile = (AbstractDankStorageTile) world.getTileEntity(pos);
      DankHandler handler = tile.itemHandler;
      return new DankContainers.DankContainer7(i,world,pos,playerInventory,playerEntity,handler);
    }

    @Override
    public ITextComponent getName() {
      return this.hasCustomName() ? new TranslationTextComponent(this.customName) : new TranslationTextComponent("container.dankstorage.dank_7");
    }

    @Override
    public Item getDank() {
      return DankStorage.Objects.dank_7.asItem();
    }
  }
}
