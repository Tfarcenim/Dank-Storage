package tfar.dankstorage.network.server;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import tfar.dankstorage.DankStorage;
import tfar.dankstorage.inventory.DankInterface;
import tfar.dankstorage.network.DankPacketHandler;
import tfar.dankstorage.network.PacketIds;

public class C2SRequestContentsPacket implements ServerPlayNetworking.PlayChannelHandler {

    public static void send(int frequency) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeInt(frequency);
        ClientPlayNetworking.send(PacketIds.request_contents, buf);
    }


    public void handle(ServerPlayer player, int frequency) {
        DankInterface dankInventoryFabric = DankStorage.getData(frequency,player.server).createInventory(frequency);
        if (dankInventoryFabric != null) {
            DankPacketHandler.sendList(player, dankInventoryFabric.getContents());
        }
    }

    @Override
    public void receive(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) {
        int frequency = buf.readInt();
        server.execute(() -> handle(player,frequency));
    }
}

