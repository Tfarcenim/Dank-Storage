package tfar.dankstorage.network;

import tfar.dankstorage.DankItemBlock;
import tfar.dankstorage.utils.Utils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;


public class CMessageToggleUseType {

  public CMessageToggleUseType(){}

    public void handle(Supplier<NetworkEvent.Context> ctx) {
      PlayerEntity player = ctx.get().getSender();

      if (player == null) return;

      ctx.get().enqueueWork(() ->  {
        if (player.getHeldItemMainhand().getItem() instanceof DankItemBlock) {
          Utils.cyclePlacement(player.getHeldItemMainhand(),player);
        }
      });
      ctx.get().setPacketHandled(true);
    }
  public static final UseType[] useTypes = UseType.values();

    public enum UseType{
    bag,chest,construction
    }
}

