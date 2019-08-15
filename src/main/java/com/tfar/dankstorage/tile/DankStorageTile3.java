package com.tfar.dankstorage.tile;

import com.tfar.dankstorage.DankStorage;
import com.tfar.dankstorage.container.DankContainer3;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.Item;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;

public class DankStorageTile3 extends AbstractDankStorageTile {

  public DankStorageTile3() {
    super(DankStorage.Objects.dank_3_tile,3,4096);
  }

  @Nullable
  @Override
  public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
    return new DankContainer3(i,world,pos,playerInventory,playerEntity);
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