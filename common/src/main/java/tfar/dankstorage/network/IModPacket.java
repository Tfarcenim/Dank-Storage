package tfar.dankstorage.network;

import net.minecraft.network.FriendlyByteBuf;

public interface IModPacket {
    void write(FriendlyByteBuf to);

}
