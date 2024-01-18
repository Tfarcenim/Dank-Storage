package tfar.dankstorage.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import tfar.dankstorage.network.client.*;

import java.util.function.Function;

public class ClientDankPacketHandler {

    public static void registerClientMessages() {
        ClientPlayNetworking.registerGlobalReceiver(PacketIds.sync_extended_slot, new S2CSyncExtendedSlotContents());

        ClientPlayNetworking.registerGlobalReceiver(PacketIds.sync_ghost_slot, wrapS2C(S2CSendGhostSlotPacket::new));

        ClientPlayNetworking.registerGlobalReceiver(PacketIds.sync_data, new S2CSyncSelected());
        ClientPlayNetworking.registerGlobalReceiver(PacketIds.sync_container, new S2CSyncContainerContents());
        ClientPlayNetworking.registerGlobalReceiver(PacketIds.sync_inventory, new S2CSyncInventory());
    }

    public static <MSG extends S2CModPacket> ClientPlayNetworking.PlayChannelHandler wrapS2C(Function<FriendlyByteBuf,MSG> decodeFunction) {
        return new ClientHandler<>(decodeFunction);
    }
}
