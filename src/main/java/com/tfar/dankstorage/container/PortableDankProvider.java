package com.tfar.dankstorage.container;

import com.tfar.dankstorage.block.DankBlock;
import com.tfar.dankstorage.inventory.PortableDankHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;

public class PortableDankProvider implements INamedContainerProvider {

  public final int tier;
  public PortableDankProvider(int tier){
    this.tier = tier;
  }

  @Override
  public ITextComponent getDisplayName() {
    return new TranslationTextComponent("test");
  }

  @Nullable
  @Override
  public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity player) {
    ItemStack bag = playerInventory.getStackInSlot(playerInventory.currentItem);
    PortableDankHandler handler = DankBlock.getHandler(bag);
    switch (tier) {
      case 1:
      default:
        return new DankContainers.PortableDankContainer1(i, playerInventory, player,handler);
      case 2:
        return new DankContainers.PortableDankContainer2(i, playerInventory, player,handler);
      case 3:
        return new DankContainers.PortableDankContainer3(i, playerInventory, player,handler);
      case 4:
        return new DankContainers.PortableDankContainer4(i, playerInventory, player,handler);
      case 5:
        return new DankContainers.PortableDankContainer5(i, playerInventory, player,handler);
      case 6:
        return new DankContainers.PortableDankContainer6(i, playerInventory, player,handler);
      case 7:
        return new DankContainers.PortableDankContainer7(i, playerInventory, player,handler);
    }
  }
}
