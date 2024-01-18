package tfar.dankstorage.network.client;

import tfar.dankstorage.network.IModPacket;

public interface S2CModPacket extends IModPacket {

    void handleClient();

}
