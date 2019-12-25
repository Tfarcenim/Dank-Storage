package com.tfar.dankstorage.network;

import com.tfar.dankstorage.container.AbstractPortableDankContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class S2CSyncNBTSize {

  private int windowId = 0;
  private int size = 0;

  public S2CSyncNBTSize() {}

  public S2CSyncNBTSize(int windowId, int slot) {
    this.windowId = windowId;
    this.size = slot;
  }

  public S2CSyncNBTSize(PacketBuffer buf) {
    this.windowId = buf.readByte();
    this.size = buf.readInt();
  }

  public void encode(PacketBuffer buf) {
    buf.writeByte(this.windowId);
    buf.writeInt(this.size);
  }

  public void handle(Supplier<NetworkEvent.Context> ctx) {
    PlayerEntity player = DistExecutor.callWhenOn(Dist.CLIENT, () -> () -> Minecraft.getInstance().player);
    if (player == null) return;

    ctx.get().enqueueWork(() -> {
      Container container = player.openContainer;
      if (container instanceof AbstractPortableDankContainer && windowId == container.windowId) {
        ((AbstractPortableDankContainer) container).nbtSize = size;
      }
    });
    ctx.get().setPacketHandled(true);
  }
}