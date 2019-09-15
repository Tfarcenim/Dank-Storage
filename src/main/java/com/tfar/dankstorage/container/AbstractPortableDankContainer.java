package com.tfar.dankstorage.container;

import com.tfar.dankstorage.inventory.LockedSlot;
import com.tfar.dankstorage.inventory.PortableDankHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

public abstract class AbstractPortableDankContainer extends AbstractAbstractDankContainer {

  protected ItemStack bag;

  public AbstractPortableDankContainer(ContainerType<?> type, int p_i50105_2_, PlayerInventory playerInventory, PlayerEntity player, PortableDankHandler handler, int rows) {
    super(type, p_i50105_2_, playerInventory, handler,rows);
    this.bag = player.getHeldItemMainhand();
    addPlayerSlots(playerInventory, playerInventory.currentItem);
  }

  @Override
  protected void addPlayerSlots(IInventory playerinventory) {}

  protected void addPlayerSlots(IInventory playerinventory, int locked) {
    int yStart = 50;
    switch (rows){
      case 9:yStart +=59;
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
        this.addSlot(new Slot(playerinventory, col + row * 9 + 9, x, y) {
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
      this.addSlot(new Slot(playerinventory, row, x, y) {
        @Override
        public int getItemStackLimit(ItemStack stack) {
          return Math.min(this.getSlotStackLimit(), stack.getMaxStackSize());
        }
      });
      else
        this.addSlot(new LockedSlot(playerinventory, row, x, y) {
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

