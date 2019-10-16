package com.tfar.dankstorage.network;

import com.tfar.dankstorage.container.AbstractAbstractDankContainer;
import com.tfar.dankstorage.utils.Utils;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class S2CMessageSyncExtendedSlotContents {

  private int windowId = 0;
  private int slot = 0;
  private ItemStack stack = ItemStack.EMPTY;

  public S2CMessageSyncExtendedSlotContents() {

  }

  public S2CMessageSyncExtendedSlotContents(int windowId, int slot, ItemStack stack) {
    this.windowId = windowId;
    this.slot = slot;
    this.stack = stack.copy();
  }

  public S2CMessageSyncExtendedSlotContents(ByteBuf buf) {
    this.windowId = buf.readByte();
    this.slot = buf.readInt();
      this.stack = Utils.readExtendedItemStack(buf);
  }

  public void encode(ByteBuf buf) {
    buf.writeByte(this.windowId);
    buf.writeInt(this.slot);
    Utils.writeExtendedItemStack(buf, stack);
  }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
      PlayerEntity player = DistExecutor.callWhenOn(Dist.CLIENT, () -> () -> Minecraft.getInstance().player);
      if (player == null) return;

      ctx.get().enqueueWork(() -> {
        if (player.openContainer instanceof AbstractAbstractDankContainer && windowId == player.openContainer.windowId) {
          player.openContainer.inventorySlots.get(slot).putStack(stack);
        }
      });


      ctx.get().setPacketHandled(true);
    }
  }