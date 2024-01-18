package tfar.dankstorage.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import tfar.dankstorage.network.server.C2SButtonPacket;
import tfar.dankstorage.network.server.C2SLockSlotPacket;
import tfar.dankstorage.network.server.C2SModPacket;
import tfar.dankstorage.network.server.C2SRequestContentsPacket;
import tfar.dankstorage.network.server.C2SScrollSlotPacket;
import tfar.dankstorage.network.server.C2SSetFrequencyPacket;

import java.util.function.Function;

public class DankPacketHandlerFabric {

    public static void registerMessages() {
        ServerPlayNetworking.registerGlobalReceiver(PacketIds.scroll, wrapC2S(C2SScrollSlotPacket::new));
        ServerPlayNetworking.registerGlobalReceiver(PacketIds.lock_slot, wrapC2S(C2SLockSlotPacket::new));
        ServerPlayNetworking.registerGlobalReceiver(PacketIds.set_frequency, wrapC2S(C2SSetFrequencyPacket::new));
        ServerPlayNetworking.registerGlobalReceiver(PacketIds.request_contents,wrapC2S(C2SRequestContentsPacket::new));
        ServerPlayNetworking.registerGlobalReceiver(PacketIds.button_action,wrapC2S(C2SButtonPacket::new));
    }

    public static <MSG extends C2SModPacket> ServerPlayNetworking.PlayChannelHandler wrapC2S(Function<FriendlyByteBuf, MSG> decodeFunction) {
        return new ServerHandler<>(decodeFunction);
    }
}
