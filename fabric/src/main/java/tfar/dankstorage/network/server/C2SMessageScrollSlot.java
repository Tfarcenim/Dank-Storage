package tfar.dankstorage.network.server;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import tfar.dankstorage.item.DankItem;
import tfar.dankstorage.network.DankPacketHandler;
import tfar.dankstorage.utils.Utils;

public class C2SMessageScrollSlot implements ServerPlayNetworking.PlayChannelHandler {

    public static void send(boolean right) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeBoolean(right);
        ClientPlayNetworking.send(DankPacketHandler.scroll, buf);
    }

    public void handle(ServerPlayer player, boolean right) {
        if (player.getMainHandItem().getItem() instanceof DankItem)
            Utils.changeSelectedSlot(player.getMainHandItem(), right,player);
        else if (player.getOffhandItem().getItem() instanceof DankItem)
            Utils.changeSelectedSlot(player.getOffhandItem(), right,player);
    }

    @Override
    public void receive(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) {
        boolean right = buf.readBoolean();
        server.execute(() -> handle(player, right));
    }
}

