package com.tfar.dankstorage.container;

import com.tfar.dankstorage.block.DankItemBlock;
import com.tfar.dankstorage.inventory.LockedSlot;
import com.tfar.dankstorage.inventory.PortableDankHandler;
import com.tfar.dankstorage.network.DankPacketHandler;
import com.tfar.dankstorage.network.S2CSyncNBTSize;
import com.tfar.dankstorage.utils.Utils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

public abstract class AbstractPortableDankContainer extends AbstractAbstractDankContainer {

  protected ItemStack bag;
  protected PortableDankHandler handler;
  public int nbtSize;

  public AbstractPortableDankContainer(ContainerType<?> type, int p_i50105_2_, PlayerInventory playerInventory, PlayerEntity player, int rows) {
    super(type, p_i50105_2_, playerInventory, rows);
    this.bag = player.getHeldItemMainhand().getItem() instanceof DankItemBlock ? player.getHeldItemMainhand() : player.getHeldItemOffhand();
    nbtSize = getNBTSize();

    handler = new PortableDankHandler(bag) {

      @Override
      protected void onLoad() {
        super.onLoad();
        if (player instanceof ServerPlayerEntity) {
          nbtSize = getNBTSize();
          DankPacketHandler.INSTANCE.sendTo(new S2CSyncNBTSize(AbstractPortableDankContainer.this.windowId, nbtSize), ((ServerPlayerEntity) player).connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);
        }
      }

      @Override
      public void onContentsChanged(int slot) {
        super.onContentsChanged(slot);
        if (player instanceof ServerPlayerEntity) {
          DankPacketHandler.INSTANCE.sendTo(new S2CSyncNBTSize(AbstractPortableDankContainer.this.windowId, getNBTSize()), ((ServerPlayerEntity) player).connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);
        }
      }
    };
    addOwnSlots();
    addPlayerSlots(new InvWrapper(playerInventory), playerInventory.currentItem);
  }

  @Override
  public PortableDankHandler getHandler() {
    return handler;
  }

  @Override
  protected void addPlayerSlots(InvWrapper playerinventory) {}

  protected void addPlayerSlots(InvWrapper playerinventory, int locked) {
    int yStart = 32 + 18 * rows;
    for (int row = 0; row < 3; ++row) {
      for (int col = 0; col < 9; ++col) {
        int x = 8 + col * 18;
        int y = row * 18 + yStart;
        this.addSlot(new SlotItemHandler(playerinventory, col + row * 9 + 9, x, y) {
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
      this.addSlot(new SlotItemHandler(playerinventory, row, x, y) {
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

  private int getNBTSize() {
    return Utils.getNbtSize(bag);
  }

  public ItemStack getBag() {
    return bag;
  }

  @Override
  public void detectAndSendChanges() {
    super.detectAndSendChanges();
    handler.writeItemStack();
  }
}

