package tfar.dankstorage.network.server;

import net.minecraft.server.level.ServerPlayer;
import tfar.dankstorage.network.IModPacket;

public interface C2SModPacket extends IModPacket {

    void handleServer(ServerPlayer player);

}
