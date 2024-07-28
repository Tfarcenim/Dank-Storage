package tfar.dankstorage.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import tfar.dankstorage.network.client.S2CModPacket;

import java.util.function.Function;

public class ClientDankPacketHandler {

    public static <MSG extends S2CModPacket> void register(Class<MSG> packetLocation, Function<FriendlyByteBuf, MSG> reader) {
        ClientPlayNetworking.registerGlobalReceiver(DankPacketHandler.packet(packetLocation), wrapS2C(reader));
    }

    public static <MSG extends S2CModPacket> ClientPlayNetworking.PlayChannelHandler wrapS2C(Function<FriendlyByteBuf,MSG> decodeFunction) {
        return new ClientHandler<>(decodeFunction);
    }


}
