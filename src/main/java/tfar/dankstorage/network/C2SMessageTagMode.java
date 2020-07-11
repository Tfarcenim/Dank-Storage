package tfar.dankstorage.network;

import tfar.dankstorage.DankItem;
import tfar.dankstorage.utils.Utils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;


public class C2SMessageTagMode {

  public C2SMessageTagMode(){}

    public void handle(Supplier<NetworkEvent.Context> ctx) {
      PlayerEntity player = ctx.get().getSender();

      if (player == null) return;

      ctx.get().enqueueWork(() ->  {
        if (player.getHeldItemMainhand().getItem() instanceof DankItem) {
          boolean toggle = Utils.oredict(player.getHeldItemMainhand());
          player.getHeldItemMainhand().getOrCreateTag().putBoolean("tag",!toggle);
        }
      });
      ctx.get().setPacketHandled(true);
    }

}

