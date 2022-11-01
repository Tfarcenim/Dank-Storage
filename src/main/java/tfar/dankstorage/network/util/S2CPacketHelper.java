package tfar.dankstorage.network.util;

import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public interface S2CPacketHelper extends PacketHelper {
    default void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(this::handleClient);
        ctx.get().setPacketHandled(true);
    }
    void handleClient();

}
