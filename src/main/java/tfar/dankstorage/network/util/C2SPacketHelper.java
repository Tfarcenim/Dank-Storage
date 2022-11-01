package tfar.dankstorage.network.util;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public interface C2SPacketHelper extends PacketHelper {
    default void handle(Supplier<NetworkEvent.Context> ctx) {
        ServerPlayer player = ctx.get().getSender();
        if (player == null) return;
        ctx.get().enqueueWork(()-> handleServer(player));
        ctx.get().setPacketHandled(true);
    }
    void handleServer(ServerPlayer player);
}
