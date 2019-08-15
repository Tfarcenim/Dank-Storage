package com.tfar.dankstorage.tile;

import com.tfar.dankstorage.DankStorage;
import com.tfar.dankstorage.container.DankContainer7;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.Item;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;

public class DankStorageTile7 extends AbstractDankStorageTile {

  public DankStorageTile7() {
    super(DankStorage.Objects.dank_7_tile,9,Integer.MAX_VALUE);
  }

  @Nullable
  @Override
  public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
    return new DankContainer7(i,world,pos,playerInventory,playerEntity);
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