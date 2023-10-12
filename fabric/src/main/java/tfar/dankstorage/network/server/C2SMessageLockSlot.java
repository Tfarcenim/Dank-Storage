package tfar.dankstorage.network.server;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.inventory.AbstractContainerMenu;
import tfar.dankstorage.container.AbstractDankMenu;
import tfar.dankstorage.world.DankInventory;
import tfar.dankstorage.network.DankPacketHandler;

public class C2SMessageLockSlot implements ServerPlayNetworking.PlayChannelHandler {

    public static void send(int slot) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeInt(slot);
        ClientPlayNetworking.send(DankPacketHandler.lock_slot, buf);
    }

    public void handle(ServerPlayer player, int slot) {
        AbstractContainerMenu container = player.containerMenu;
        if (container instanceof AbstractDankMenu dankContainer) {
            DankInventory inventory = dankContainer.dankInventory;
            inventory.toggleGhostItem(slot);
        }
    }

    @Override
    public void receive(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) {
        int slot = buf.readInt();
        server.execute(() -> handle(player, slot));
    }
}

