package tfar.dankstorage.network.server;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.HitResult;
import tfar.dankstorage.item.DankItem;
import tfar.dankstorage.network.DankPacketHandler;
import tfar.dankstorage.utils.Utils;


public class C2SMessagePickBlock implements ServerPlayNetworking.PlayChannelHandler {

    public static void send(ItemStack stack) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeItem(stack);
        ClientPlayNetworking.send(DankPacketHandler.pick_block, buf);
    }

    public void handle(ServerPlayer player, ItemStack stack) {
        if (player.getMainHandItem().getItem() instanceof DankItem)
            Utils.setPickSlot(player.level,player.getMainHandItem(), stack);
        else if (player.getOffhandItem().getItem() instanceof DankItem)
            Utils.setPickSlot(player.level,player.getOffhandItem(), stack);
    }

    @Override
    public void receive(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) {
        ItemStack stack = buf.readItem();
        server.execute(() -> handle(player, stack));
    }
}
