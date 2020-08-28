package tfar.dankstorage.network;

import tfar.dankstorage.container.AbstractDankContainer;
import tfar.dankstorage.utils.PacketBufferEX;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class S2CSyncExtendedSlotContents {

  private int windowId = 0;
  private int slot = 0;
  private ItemStack stack = ItemStack.EMPTY;

  public S2CSyncExtendedSlotContents() {}

  public S2CSyncExtendedSlotContents(int windowId, int slot, ItemStack stack) {
    this.windowId = windowId;
    this.slot = slot;
    this.stack = stack.copy();
  }

  public S2CSyncExtendedSlotContents(PacketBuffer buf) {
    buf = new PacketBufferEX(buf);
    this.windowId = buf.readByte();
    this.slot = buf.readInt();
    this.stack = ((PacketBufferEX) buf).readExtendedItemStack();
  }

  public void encode(PacketBuffer buf) {
    buf = new PacketBufferEX(buf);
    buf.writeByte(this.windowId);
    buf.writeInt(this.slot);
    ((PacketBufferEX) buf).writeExtendedItemStack(stack);
  }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
      PlayerEntity player = DistExecutor.callWhenOn(Dist.CLIENT, () -> () -> Minecraft.getInstance().player);
      if (player == null) return;

      ctx.get().enqueueWork(() -> {
        if (player.openContainer instanceof AbstractDankContainer && windowId == player.openContainer.windowId) {
          player.openContainer.inventorySlots.get(slot).putStack(stack);
        }
      });


      ctx.get().setPacketHandled(true);
    }
  }