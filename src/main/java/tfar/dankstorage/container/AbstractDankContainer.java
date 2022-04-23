package tfar.dankstorage.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.*;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SSetSlotPacket;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntReferenceHolder;
import net.minecraftforge.fml.network.NetworkDirection;
import tfar.dankstorage.inventory.CappedSlot;
import tfar.dankstorage.inventory.DankHandler;
import tfar.dankstorage.inventory.DankSlot;
import tfar.dankstorage.inventory.PortableDankHandler;
import tfar.dankstorage.network.DankPacketHandler;
import tfar.dankstorage.network.S2CSyncExtendedSlotContents;
import tfar.dankstorage.utils.Utils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class AbstractDankContainer extends Container {

  public final int rows;
  public final IIntArray propertyDelegate;
  public final DankHandler dankHandler;
  public final PlayerInventory playerInventory;

  public AbstractDankContainer(ContainerType<?> type, int id,PlayerInventory playerInv, DankHandler dankHandler,IIntArray propertyDelegate) {
    super(type, id);
    this.rows = dankHandler.getSlots() / 9;
    this.dankHandler = dankHandler;
    this.propertyDelegate = propertyDelegate;
    playerInventory = playerInv;
    trackIntArray(propertyDelegate);
    dankHandler.onOpen(playerInv.player);
  }

  public void addOwnSlots(boolean portable) {
    int slotIndex = 0;
    for (int row = 0; row < rows; ++row) {
      for (int col = 0; col < 9; ++col) {
        int x = 8 + col * 18;
        int y = row * 18 + 18;
        if (portable) {
          this.addSlot(new DankSlot(dankHandler, slotIndex, x, y) {
            @Override
            public void onSlotChanged() {
              super.onSlotChanged();
              if (!playerInventory.player.world.isRemote) {
                propertyDelegate.set(AbstractDankContainer.this.dankHandler.getSlots(), Utils.getNbtSize(((PortableDankHandler) dankHandler).bag));
              }
            }
          });
        } else {
          this.addSlot(new DankSlot(dankHandler, slotIndex, x, y));
        }
        slotIndex++;
      }
    }
  }

  protected void addPlayerSlots(PlayerInventory playerinventory) {
    int yStart = 32 + 18 * rows;
    for (int row = 0; row < 3; ++row) {
      for (int col = 0; col < 9; ++col) {
        int x = 8 + col * 18;
        int y = row * 18 + yStart;
        this.addSlot(new CappedSlot(playerinventory, col + row * 9 + 9, x, y));
      }
    }

    for (int row = 0; row < 9; ++row) {
      int x = 8 + row * 18;
      int y = yStart + 58;
      this.addSlot(new CappedSlot(playerinventory, row, x, y));
    }
  }

  @Nonnull
  public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {

    ItemStack itemstack = ItemStack.EMPTY;
    Slot slot = this.inventorySlots.get(index);

    if (slot != null && slot.getHasStack() /*&& (!locked || slot.getStack().getCount()> 1)*/) {
      ItemStack slotStack = slot.getStack();

      itemstack = slotStack.copy();

      if (index < rows * 9) {
        if (!this.mergeItemStack(slotStack, rows * 9, this.inventorySlots.size(), true)) {
          return ItemStack.EMPTY;
        }
      } else if (!this.mergeItemStack(slotStack, 0, rows * 9, false)) {
        return ItemStack.EMPTY;
      }

      if (slotStack.isEmpty()) {
        slot.putStack(ItemStack.EMPTY);
      } else {
        slot.onSlotChanged();
      }
    }
    return itemstack;
  }

  @Nonnull
  @Override
  public ItemStack func_241440_b_(int slotId, int dragType, ClickType clickTypeIn, PlayerEntity player) {
    boolean locked = slotId >= 0 && slotId < (rows * 9) && propertyDelegate.get(slotId) == 1;
    ItemStack returnStack = ItemStack.EMPTY;
    PlayerInventory PlayerInventory = player.inventory;

    if (clickTypeIn == ClickType.QUICK_CRAFT) {
      int j1 = this.dragEvent;
      this.dragEvent = getDragEvent(dragType);

      if ((j1 != 1 || this.dragEvent != 2) && j1 != this.dragEvent) {
        this.resetDrag();
      } else if (PlayerInventory.getItemStack().isEmpty()) {
        this.resetDrag();
      } else if (this.dragEvent == 0) {
        this.dragMode = extractDragMode(dragType);

        if (isValidDragMode(this.dragMode, player)) {
          this.dragEvent = 1;
          this.dragSlots.clear();
        } else {
          this.resetDrag();
        }
      } else if (this.dragEvent == 1) {
        Slot slot = this.inventorySlots.get(slotId);
        ItemStack mouseStack = PlayerInventory.getItemStack();

        if (slot != null && canAddItemToSlot(slot, mouseStack, true) && slot.isItemValid(mouseStack) && (this.dragMode == 2 || mouseStack.getCount() > this.dragSlots.size()) && this.canDragIntoSlot(slot)) {
          this.dragSlots.add(slot);
        }
      } else if (this.dragEvent == 2) {
        if (!this.dragSlots.isEmpty()) {
          ItemStack mouseStackCopy = PlayerInventory.getItemStack().copy();
          int k1 = PlayerInventory.getItemStack().getCount();

          for (Slot dragSlot : this.dragSlots) {
            ItemStack mouseStack = PlayerInventory.getItemStack();

            if (dragSlot != null && canAddItemToSlot(dragSlot, mouseStack, true) && dragSlot.isItemValid(mouseStack) && (this.dragMode == 2 || mouseStack.getCount() >= this.dragSlots.size()) && this.canDragIntoSlot(dragSlot)) {
              ItemStack itemstack14 = mouseStackCopy.copy();
              int j3 = dragSlot.getHasStack() ? dragSlot.getStack().getCount() : 0;
              computeStackSize(this.dragSlots, this.dragMode, itemstack14, j3);
              int k3 = dragSlot.getItemStackLimit(itemstack14);

              if (itemstack14.getCount() > k3) {
                itemstack14.setCount(k3);
              }

              k1 -= itemstack14.getCount() - j3;
              dragSlot.putStack(itemstack14);
            }
          }

          mouseStackCopy.setCount(k1);
          PlayerInventory.setItemStack(mouseStackCopy);
        }

        this.resetDrag();
      } else {
        this.resetDrag();
      }
    } else if (this.dragEvent != 0) {
      this.resetDrag();
    } else if ((clickTypeIn == ClickType.PICKUP || clickTypeIn == ClickType.QUICK_MOVE) && (dragType == 0 || dragType == 1)) {
      if (slotId == -999) {
        if (!PlayerInventory.getItemStack().isEmpty()) {
          if (dragType == 0) {
            player.dropItem(PlayerInventory.getItemStack(), true);
            PlayerInventory.setItemStack(ItemStack.EMPTY);
          }

          if (dragType == 1) {
            player.dropItem(PlayerInventory.getItemStack().split(1), true);
          }
        }
      } else if (clickTypeIn == ClickType.QUICK_MOVE) {
        if (slotId < 0) {
          return ItemStack.EMPTY;
        }

        Slot slot = this.inventorySlots.get(slotId);

        if (slot == null || !slot.canTakeStack(player)) {
          return ItemStack.EMPTY;
        }

        for (ItemStack itemstack7 = this.transferStackInSlot(player, slotId);
             !itemstack7.isEmpty() && (ItemStack.areItemsEqual(slot.getStack(), itemstack7));
             itemstack7 = this.transferStackInSlot(player, slotId)) {
          returnStack = itemstack7.copy();
        }
      } else {
        if (slotId < 0) {
          return ItemStack.EMPTY;
        }

        Slot slot = this.inventorySlots.get(slotId);

        if (slot != null) {
          ItemStack slotStack = slot.getStack();
          ItemStack mouseStack = PlayerInventory.getItemStack();

          if (!slotStack.isEmpty()) {
            returnStack = slotStack.copy();//!locked ? ItemHandlerHelper.copyStackWithSize(slotStack, slotStack.getCount() - 1) : slotStack.copy();
          }

          if (slotStack.isEmpty()) {
            if (!mouseStack.isEmpty() && slot.isItemValid(mouseStack)) {
              int i3 = dragType == 0 ? mouseStack.getCount() : 1;

              if (i3 > slot.getItemStackLimit(mouseStack)) {
                i3 = slot.getItemStackLimit(mouseStack);
              }

              slot.putStack(mouseStack.split(i3));
            }
          } else if (slot.canTakeStack(player)) {
            if (mouseStack.isEmpty()) {
              if (slotStack.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
                PlayerInventory.setItemStack(ItemStack.EMPTY);
              } else {
                int toMove;
                if (slot instanceof DankSlot) {
                  if (slotStack.getMaxStackSize() < slotStack.getCount())
                  toMove = dragType == 0 ? slotStack.getMaxStackSize() : (slotStack.getMaxStackSize() + 1) / 2;
                  else toMove = dragType == 0 ? slotStack.getCount() : (slotStack.getCount() + 1) / 2;
                } else {
                  toMove = dragType == 0 ? slotStack.getCount() : (slotStack.getCount() + 1) / 2;
                }
                //int toMove = dragType == 0 ? slotStack.getCount() : (slotStack.getCount() + 1) / 2;
                PlayerInventory.setItemStack(slot.decrStackSize(toMove));

                if (slotStack.isEmpty()) {
                  slot.putStack(ItemStack.EMPTY);
                }

                slot.onTake(player, PlayerInventory.getItemStack());
              }
            } else if (slot.isItemValid(mouseStack)) {
              if (slotStack.getItem() == mouseStack.getItem() && ItemStack.areItemStackTagsEqual(slotStack, mouseStack)) {
                int k2 = dragType == 0 ? mouseStack.getCount() : 1;

                if (k2 > slot.getItemStackLimit(mouseStack) - slotStack.getCount()) {
                  k2 = slot.getItemStackLimit(mouseStack) - slotStack.getCount();
                }

                mouseStack.shrink(k2);
                slotStack.grow(k2);
              } else if (mouseStack.getCount() <= slot.getItemStackLimit(mouseStack) && slotStack.getCount() <= slotStack.getMaxStackSize()) {
                slot.putStack(mouseStack);
                PlayerInventory.setItemStack(slotStack);
              }
            } else if (slotStack.getItem() == mouseStack.getItem() && mouseStack.getMaxStackSize() > 1 && ItemStack.areItemStackTagsEqual(slotStack, mouseStack) && !slotStack.isEmpty()) {
              int j2 = slotStack.getCount();

              if (j2 + mouseStack.getCount() <= mouseStack.getMaxStackSize()) {
                mouseStack.grow(j2);
                slotStack = slot.decrStackSize(j2);

                if (slotStack.isEmpty()) {
                  slot.putStack(ItemStack.EMPTY);
                }

                slot.onTake(player, PlayerInventory.getItemStack());
              }
            }
          }

          slot.onSlotChanged();
        }
      }
    } else if (clickTypeIn == ClickType.SWAP && dragType >= 0 && dragType < 9) {
      //just don't do anything as swapping slots is unsupported
      /*
      Slot slot4 = this.inventorySlots.get(slotId);
      ItemStack itemstack6 = PlayerInventory.getStackInSlot(dragType);
      ItemStack slotStack = slot4.getStack();

      if (!itemstack6.isEmpty() || !slotStack.isEmpty()) {
        if (itemstack6.isEmpty()) {
          if (slot4.canTakeStack(player)) {
            int maxAmount = slotStack.getMaxStackSize();
            if (slotStack.getCount() > maxAmount) {
              ItemStack newSlotStack = slotStack.copy();
              ItemStack takenStack = newSlotStack.split(maxAmount);
              PlayerInventory.setInventorySlotContents(dragType, takenStack);
              //slot4.onSwapCraft(takenStack.getCount());
              slot4.putStack(newSlotStack);
              slot4.onTake(player, takenStack);
            } else {
              PlayerInventory.setInventorySlotContents(dragType, slotStack);
              //slot4.onSwapCraft(slotStack.getCount());
              slot4.putStack(ItemStack.EMPTY);
              slot4.onTake(player, slotStack);
            }
          }
        } else if (slotStack.isEmpty()) {
          if (slot4.isItemValid(itemstack6)) {
            int l1 = slot4.getItemStackLimit(itemstack6);

            if (itemstack6.getCount() > l1) {
              slot4.putStack(itemstack6.split(l1));
            } else {
              slot4.putStack(itemstack6);
              PlayerInventory.setInventorySlotContents(dragType, ItemStack.EMPTY);
            }
          }
        } else if (slot4.canTakeStack(player) && slot4.isItemValid(itemstack6)) {
          int i2 = slot4.getItemStackLimit(itemstack6);

          if (itemstack6.getCount() > i2) {
            slot4.putStack(itemstack6.split(i2));
            slot4.onTake(player, slotStack);

            if (!PlayerInventory.addItemStackToInventory(slotStack)) {
              player.dropItem(slotStack, true);
            }
          } else {
            slot4.putStack(itemstack6);
            if (slotStack.getCount() > slotStack.getMaxStackSize()) {
              ItemStack remainder = slotStack.copy();
              PlayerInventory.setInventorySlotContents(dragType, remainder.split(slotStack.getMaxStackSize()));
              if (!PlayerInventory.addItemStackToInventory(remainder)) {
                player.dropItem(remainder, true);
              }
            } else {
              PlayerInventory.setInventorySlotContents(dragType, slotStack);
            }
            slot4.onTake(player, slotStack);
          }
        }
      }*/
    } else if (clickTypeIn == ClickType.CLONE && player.abilities.isCreativeMode && PlayerInventory.getItemStack().isEmpty() && slotId >= 0) {
      Slot slot3 = this.inventorySlots.get(slotId);

      if (slot3 != null && slot3.getHasStack()) {
        ItemStack itemstack5 = slot3.getStack().copy();
        itemstack5.setCount(itemstack5.getMaxStackSize());
        PlayerInventory.setItemStack(itemstack5);
      }
    } else if (clickTypeIn == ClickType.THROW && PlayerInventory.getItemStack().isEmpty() && slotId >= 0) {
      Slot slot = this.inventorySlots.get(slotId);

      if (slot != null && slot.getHasStack() && slot.canTakeStack(player)) {
        ItemStack itemstack4 = slot.decrStackSize(dragType == 0 ? 1 : slot.getStack().getCount());
        slot.onTake(player, itemstack4);
        player.dropItem(itemstack4, true);
      }
    } else if (clickTypeIn == ClickType.PICKUP_ALL && slotId >= 0 && false) {
      Slot slot = this.inventorySlots.get(slotId);
      ItemStack mouseStack = PlayerInventory.getItemStack();

      if (!mouseStack.isEmpty() && (slot == null || !slot.getHasStack() || !slot.canTakeStack(player))) {
        int i = dragType == 0 ? 0 : this.inventorySlots.size() - 1;
        int j = dragType == 0 ? 1 : -1;

        for (int k = 0; k < 2; ++k) {
          for (int l = i; l >= 0 && l < this.inventorySlots.size() && mouseStack.getCount() < mouseStack.getMaxStackSize(); l += j) {
            Slot slot1 = this.inventorySlots.get(l);

            if (slot1.getHasStack() && DockContainer.canAddItemToSlot(slot1, mouseStack, true) && slot1.canTakeStack(player) && this.canMergeSlot(mouseStack, slot1)) {
              ItemStack itemstack2 = slot1.getStack();

              if (k != 0 || itemstack2.getCount() < slot1.getItemStackLimit(itemstack2)) {
                int i1 = Math.min(mouseStack.getMaxStackSize() - mouseStack.getCount(), itemstack2.getCount());
                ItemStack itemstack3 = slot1.decrStackSize(i1);
                mouseStack.grow(i1);

                if (itemstack3.isEmpty()) {
                  slot1.putStack(ItemStack.EMPTY);
                }

                slot1.onTake(player, itemstack3);
              }
            }
          }
        }
      }

      this.detectAndSendChanges();
    }

    if (returnStack.getCount() > 64) {
      returnStack = returnStack.copy();
      returnStack.setCount(64);
    }

    return returnStack;
  }

  @Override
  public final boolean canInteractWith(@Nonnull PlayerEntity playerIn) {
    return dankHandler.canPlayerUse(playerIn);
  }

  @Override
  protected boolean mergeItemStack(ItemStack stack, int startIndex, int endIndex, boolean reverseDirection) {
    boolean flag = false;
    int i = startIndex;

    if (reverseDirection) {
      i = endIndex - 1;
    }

    while (!stack.isEmpty()) {
      if (reverseDirection) {
        if (i < startIndex) break;
      } else {
        if (i >= endIndex) break;
      }

      Slot slot = this.inventorySlots.get(i);
      ItemStack slotStack = slot.getStack();

      if (!slotStack.isEmpty() && slotStack.getItem() == stack.getItem() && ItemStack.areItemStackTagsEqual(stack, slotStack)) {
        int j = slotStack.getCount() + stack.getCount();
        int maxSize = slot.getItemStackLimit(slotStack);

        if (j <= maxSize) {
          stack.setCount(0);
          slotStack.setCount(j);
          slot.onSlotChanged();
          flag = true;
        } else if (slotStack.getCount() < maxSize) {
          stack.shrink(maxSize - slotStack.getCount());
          slotStack.setCount(maxSize);
          slot.onSlotChanged();
          flag = true;
        }
      }

      i += (reverseDirection) ? -1 : 1;
    }

    if (!stack.isEmpty()) {
      if (reverseDirection) i = endIndex - 1;
      else i = startIndex;

      while (true) {
        if (reverseDirection) {
          if (i < startIndex) break;
        } else {
          if (i >= endIndex) break;
        }

        Slot slot1 = this.inventorySlots.get(i);
        ItemStack itemstack1 = slot1.getStack();

        if (itemstack1.isEmpty() && slot1.isItemValid(stack)) {
          if (stack.getCount() > slot1.getItemStackLimit(stack)) {
            slot1.putStack(stack.split(slot1.getItemStackLimit(stack)));
          } else {
            slot1.putStack(stack.split(stack.getCount()));
          }

          slot1.onSlotChanged();
          flag = true;
          break;
        }

        i += (reverseDirection) ? -1 : 1;
      }
    }

    return flag;
  }

  public static boolean canAddItemToSlot(@Nullable Slot slot,@Nonnull ItemStack stack, boolean stackSizeMatters) {
    boolean flag = slot == null || !slot.getHasStack();
    if (slot != null) {
      ItemStack slotStack = slot.getStack();

      if (!flag && stack.isItemEqual(slotStack) && ItemStack.areItemStackTagsEqual(slotStack, stack)) {
        return slotStack.getCount() + (stackSizeMatters ? 0 : stack.getCount()) <= slot.getItemStackLimit(slotStack);
      }
    }
    return flag;
  }

  //need to override
  @Override
  public void detectAndSendChanges() {
    for (int i = 0; i < this.inventorySlots.size(); ++i) {
      ItemStack itemstack = (this.inventorySlots.get(i)).getStack();
      ItemStack itemstack1 = this.inventoryItemStacks.get(i);

      if (!ItemStack.areItemStacksEqual(itemstack1, itemstack)) {
        itemstack1 = itemstack.isEmpty() ? ItemStack.EMPTY : itemstack.copy();
        this.inventoryItemStacks.set(i, itemstack1);

        for (IContainerListener listener : this.listeners) {
          if (listener instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) listener;
            this.syncSlot(player, i, itemstack1);
          }
        }
      }

      for(int j = 0; j < this.trackedIntReferences.size(); ++j) {
        IntReferenceHolder intreferenceholder = this.trackedIntReferences.get(j);
        if (intreferenceholder.isDirty()) {
          for(IContainerListener icontainerlistener1 : this.listeners) {
            icontainerlistener1.sendWindowProperty(this, j, intreferenceholder.get());
          }
        }
      }

    }
  }

  @Override
  public void addListener(IContainerListener listener) {
    if (this.listeners.contains(listener)) {
      throw new IllegalArgumentException("Listener already listening");
    } else {
      this.listeners.add(listener);
      if (listener instanceof ServerPlayerEntity) {
        ServerPlayerEntity player = (ServerPlayerEntity) listener;
        this.syncInventory(player);
      }
      this.detectAndSendChanges();
    }
  }

  @Override
  public void onContainerClosed(PlayerEntity playerIn) {
    super.onContainerClosed(playerIn);
    dankHandler.onClose(playerIn);
  }

  public void syncInventory(ServerPlayerEntity player) {
    for (int i = 0; i < this.inventorySlots.size(); i++) {
      ItemStack stack = (this.inventorySlots.get(i)).getStack();
      DankPacketHandler.INSTANCE.sendTo(new S2CSyncExtendedSlotContents(this.windowId, i, stack),player.connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);
    }
    player.connection.sendPacket(new SSetSlotPacket(-1, -1, player.inventory.getItemStack()));
  }

  public void syncSlot(ServerPlayerEntity player, int slot, ItemStack stack) {
    DankPacketHandler.INSTANCE.sendTo(new S2CSyncExtendedSlotContents(this.windowId, slot, stack), player.connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);
  }
}
