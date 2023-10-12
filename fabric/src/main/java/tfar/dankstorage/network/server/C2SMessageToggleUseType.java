package tfar.dankstorage.network.server;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.player.Player;
import tfar.dankstorage.item.DankItem;
import tfar.dankstorage.network.DankPacketHandler;
import tfar.dankstorage.utils.Utils;


public class C2SMessageToggleUseType implements ServerPlayNetworking.PlayChannelHandler {

    public static final UseType[] useTypes = UseType.values();

    public static void send() {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        ClientPlayNetworking.send(DankPacketHandler.toggle_use, buf);
    }

    public void handle(ServerPlayer player) {
        if (player.getMainHandItem().getItem() instanceof DankItem)
            Utils.cyclePlacement(player.getMainHandItem(), player);
        else if (player.getOffhandItem().getItem() instanceof DankItem)
            Utils.cyclePlacement(player.getOffhandItem(), player);
    }

    @Override
    public void receive(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) {
        server.execute(() -> handle(player));
    }

    public enum UseType {
        bag, construction
    }
}

