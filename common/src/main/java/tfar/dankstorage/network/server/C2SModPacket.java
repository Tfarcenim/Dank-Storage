package tfar.dankstorage.network.server;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

public interface C2SModPacket extends CustomPacketPayload {

    void handleServer(ServerPlayer player);

}
