package tfar.dankstorage.network.server;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import tfar.dankstorage.network.DankPacketHandler;
import tfar.dankstorage.utils.CommonUtils;

public class C2SSetFrequencyPacket implements ServerPlayNetworking.PlayChannelHandler {

    public static void send(int id,boolean set) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeInt(id);
        buf.writeBoolean(set);
        ClientPlayNetworking.send(DankPacketHandler.set_id, buf);
    }

    public void handle(ServerPlayer player, int frequency,boolean set) {
        CommonUtils.setTxtColor(player, frequency, set);
    }

    @Override
    public void receive(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) {
        int id = buf.readInt();
        boolean set = buf.readBoolean();
        server.execute(() -> handle(player, id,set));
    }
}

