package com.tfar.dankstorage.container;

import com.tfar.dankstorage.inventory.PortableDankHandler;
import com.tfar.dankstorage.network.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.world.INamedMenuProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

import javax.annotation.Nullable;

public class PortableDankProvider implements INamedMenuProvider {

  public final int tier;
  public PortableDankProvider(int tier){
    this.tier = tier;
  }

  @Override
  public ITextComponent getDisplayName() {
    return new TextComponentTranslation("test");
  }

  @Nullable
  @Override
  public Container createMenu(int i, InventoryPlayer playerInventory, EntityPlayer player) {
    ItemStack bag = playerInventory.getStackInSlot(playerInventory.currentItem);
    PortableDankHandler handler = Utils.getHandler(bag);
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
