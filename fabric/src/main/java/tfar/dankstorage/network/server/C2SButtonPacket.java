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
import tfar.dankstorage.network.DankPacketHandler;
import tfar.dankstorage.utils.Utils;
import tfar.dankstorage.world.DankInventoryFabric;


public class C2SButtonPacket implements ServerPlayNetworking.PlayChannelHandler {


    public static void send(Action action) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeInt(action.ordinal());
        ClientPlayNetworking.send(DankPacketHandler.button_action, buf);
    }

    public void handleInternal(ServerPlayer player, Action action) {
        AbstractContainerMenu container = player.containerMenu;

        if (action.requiresContainer) {
            if (container instanceof AbstractDankMenu dankContainer) {
                DankInventoryFabric inventory = dankContainer.dankInventory;
                switch (action) {
                    case LOCK_FREQUENCY -> inventory.toggleFrequencyLock();
                    case SORT -> inventory.sort();
                    case COMPRESS -> inventory.compress(player);
                }
            }
        } else {
            switch (action) {
                case TOGGLE_TAG -> Utils.toggleTagMode(player);
                case TOGGLE_PICKUP -> Utils.togglePickupMode(player);
                case TOGGLE_USE_TYPE -> Utils.toggleUseType(player);
            }
        }
    }

    @Override
    public void receive(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) {
        Action action = Action.values()[buf.readInt()];
        server.execute(() -> handleInternal(player,action));
    }

    public enum Action {
        LOCK_FREQUENCY(true),PICK_BLOCK(false),SORT(true),
        TOGGLE_TAG(true),TOGGLE_PICKUP(false),TOGGLE_USE_TYPE(false),COMPRESS(true);
        private final boolean requiresContainer;
        Action(boolean requiresContainer) {
            this.requiresContainer = requiresContainer;
        }
    }
}
