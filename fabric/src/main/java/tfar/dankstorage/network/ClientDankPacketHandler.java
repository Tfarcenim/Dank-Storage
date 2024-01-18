package tfar.dankstorage.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import tfar.dankstorage.network.client.*;

import java.util.function.Function;

public class ClientDankPacketHandler {

    public static void registerClientMessages() {
        ClientPlayNetworking.registerGlobalReceiver(PacketIds.sync_extended_slot, wrapS2C(S2CSendExtendedSlotChangePacket::new));

        ClientPlayNetworking.registerGlobalReceiver(PacketIds.sync_ghost_slot, wrapS2C(S2CSendGhostSlotPacket::new));

        ClientPlayNetworking.registerGlobalReceiver(PacketIds.sync_selected_dank_item, wrapS2C(S2CSyncSelectedDankItemPacket::new));
        ClientPlayNetworking.registerGlobalReceiver(PacketIds.initial_sync_container, wrapS2C(S2CInitialSyncContainerPacket::new));
        ClientPlayNetworking.registerGlobalReceiver(PacketIds.sync_dank_inventory, wrapS2C(S2CContentsForDisplayPacket::new));
    }

    public static <MSG extends S2CModPacket> ClientPlayNetworking.PlayChannelHandler wrapS2C(Function<FriendlyByteBuf,MSG> decodeFunction) {
        return new ClientHandler<>(decodeFunction);
    }
}
