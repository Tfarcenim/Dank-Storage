package tfar.dankstorage.network;

import tfar.dankstorage.container.AbstractAbstractDankContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;


public class C2SMessageLockSlot {

  int slot;

  public C2SMessageLockSlot(){}

  public C2SMessageLockSlot(int slot){ this.slot = slot;}

  //decode
  public C2SMessageLockSlot(PacketBuffer buf) {
    this.slot = buf.readInt();
  }

  public void encode(PacketBuffer buf) {
    buf.writeInt(slot);
  }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
      PlayerEntity player = ctx.get().getSender();
      if (player == null) return;
      ctx.get().enqueueWork(  ()->  {
        Container container = player.openContainer;
        if (container instanceof AbstractAbstractDankContainer) {
          ((AbstractAbstractDankContainer) container).getHandler().lockSlot(slot);
        }
      });
      ctx.get().setPacketHandled(true);
    }
}

