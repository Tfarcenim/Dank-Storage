package com.tfar.dankstorage.network;

import java.util.function.Supplier;

import com.tfar.dankstorage.container.AbstractDankContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;


public class MessageDankSlotContents {

  private int windowId = 0;
  private int slot = 0;
  private ItemStack stack = ItemStack.EMPTY;

  public MessageDankSlotContents(){}

  public MessageDankSlotContents(int windowId, int slot, ItemStack stack) {
    this.windowId = windowId;
    this.slot = slot;
    this.stack = stack.copy();
  }

  public void encode(PacketBuffer buf) {
    buf.writeInt(this.windowId);
    buf.writeInt(this.slot);
    NetworkUtils.writeExtendedItemStack(buf, stack);
  }

  public static MessageDankSlotContents decode(PacketBuffer buffer) {
    int windowId = buffer.readInt();
    int slot = buffer.readInt();
    ItemStack stack = NetworkUtils.readExtendedItemStack(buffer);
    return new MessageDankSlotContents(windowId,slot,stack);
  }

    public void handle(Supplier<NetworkEvent.Context> ctx) {

      PlayerEntity player = DistExecutor.callWhenOn(Dist.CLIENT,() -> () -> Minecraft.getInstance().player);

      if (player == null) return;

      ctx.get().enqueueWork(  ()->  {
        if (player.openContainer instanceof AbstractDankContainer && this.windowId == player.openContainer.windowId) {
          player.openContainer.inventorySlots.get(this.slot).putStack(this.stack);
        }
      });
      ctx.get().setPacketHandled(true);
    }


}

