package tfar.dankstorage.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import tfar.dankstorage.network.client.S2CModPacket;

import java.util.function.Function;

public class ClientHandler<MSG extends S2CModPacket> implements ClientPlayNetworking.PlayChannelHandler {

    private final Function<FriendlyByteBuf, MSG> decodeFunction;

    public ClientHandler(Function<FriendlyByteBuf, MSG> decodeFunction) {
        this.decodeFunction = decodeFunction;
    }

    @Override
    public void receive(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
        MSG decode = decodeFunction.apply(buf);
        client.execute(decode::handleClient);
    }
}
