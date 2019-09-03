package com.tfar.dankstorage.container;

import com.tfar.dankstorage.inventory.LockedSlot;
import com.tfar.dankstorage.inventory.PortableDankHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public abstract class AbstractPortableDankContainer extends AbstractAbstractDankContainer {

  protected ItemStack bag;

  public AbstractPortableDankContainer(InventoryPlayer playerInventory, EntityPlayer player, PortableDankHandler handler, int rows) {
    super(playerInventory,player,handler,rows);
    this.bag = player.getHeldItemMainhand();
    addPlayerSlots(playerInventory, playerInventory.currentItem);
  }

  @Override
  protected void addPlayerSlots(IInventory playerinventory) {}

  protected void addPlayerSlots(IInventory playerinventory, int locked) {
    int yStart = 50;
    switch (rows){
      case 9:yStart +=54;
      case 6:yStart +=18;
      case 5:yStart +=18;
      case 4:yStart +=20;
      case 3:yStart +=16;
      case 2:yStart +=18;
    }
    for (int row = 0; row < 3; ++row) {
      for (int col = 0; col < 9; ++col) {
        int x = 8 + col * 18;
        int y = row * 18 + yStart;
        this.addSlotToContainer(new Slot(playerinventory, col + row * 9 + 9, x, y) {
          @Override
          public int getItemStackLimit(ItemStack stack) {
            return Math.min(this.getSlotStackLimit(), stack.getMaxStackSize());
          }
        });
      }
    }

    for (int row = 0; row < 9; ++row) {
      int x = 8 + row * 18;
      int y = yStart + 58;
      if (row != locked)
      this.addSlotToContainer(new Slot(playerinventory, row, x, y) {
        @Override
        public int getItemStackLimit(ItemStack stack) {
          return Math.min(this.getSlotStackLimit(), stack.getMaxStackSize());
        }
      });
      else
        this.addSlotToContainer(new LockedSlot(playerinventory, row, x, y) {
        @Override
        public int getItemStackLimit(ItemStack stack) {
          return Math.min(this.getSlotStackLimit(), stack.getMaxStackSize());
        }
      });
    }
  }

  @Override
  public void detectAndSendChanges() {
    super.detectAndSendChanges();
    ((PortableDankHandler)handler).writeItemStack();
  }
}

