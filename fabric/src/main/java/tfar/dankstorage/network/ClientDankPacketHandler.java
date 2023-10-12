package tfar.dankstorage.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import tfar.dankstorage.network.client.*;

public class ClientDankPacketHandler {

    public static void registerClientMessages() {
        ClientPlayNetworking.registerGlobalReceiver(DankPacketHandler.sync_slot, new S2CSyncExtendedSlotContents());
        ClientPlayNetworking.registerGlobalReceiver(DankPacketHandler.sync_ghost, new S2CSyncGhostItemPacket());
        ClientPlayNetworking.registerGlobalReceiver(DankPacketHandler.sync_data, new S2CSyncSelected());
        ClientPlayNetworking.registerGlobalReceiver(DankPacketHandler.sync_container, new S2CSyncContainerContents());
        ClientPlayNetworking.registerGlobalReceiver(DankPacketHandler.sync_inventory, new S2CSyncInventory());
    }
}
