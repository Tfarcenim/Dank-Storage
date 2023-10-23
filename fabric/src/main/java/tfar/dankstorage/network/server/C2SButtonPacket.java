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
import tfar.dankstorage.inventory.DankInterface;
import tfar.dankstorage.menu.AbstractDankMenu;
import tfar.dankstorage.network.DankPacketHandler;
import tfar.dankstorage.utils.ButtonAction;
import tfar.dankstorage.utils.CommonUtils;
import tfar.dankstorage.world.DankInventoryFabric;


public class C2SButtonPacket implements ServerPlayNetworking.PlayChannelHandler {


    public static void send(ButtonAction buttonAction) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeInt(buttonAction.ordinal());
        ClientPlayNetworking.send(DankPacketHandler.button_action, buf);
    }

    public void handleInternal(ServerPlayer player, ButtonAction buttonAction) {
        AbstractContainerMenu container = player.containerMenu;

        if (buttonAction.requiresContainer) {
            if (container instanceof AbstractDankMenu dankContainer) {
                DankInterface inventory = dankContainer.dankInventory;
                switch (buttonAction) {
                    case LOCK_FREQUENCY -> inventory.toggleFrequencyLock();
                    case SORT -> inventory.sort();
                    case COMPRESS -> inventory.compress(player);
                }
            }
        } else {
            switch (buttonAction) {
                case TOGGLE_TAG -> CommonUtils.toggleTagMode(player);
                case TOGGLE_PICKUP -> CommonUtils.togglePickupMode(player);
                case TOGGLE_USE_TYPE -> CommonUtils.toggleUseType(player);
            }
        }
    }

    @Override
    public void receive(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) {
        ButtonAction buttonAction = ButtonAction.values()[buf.readInt()];
        server.execute(() -> handleInternal(player, buttonAction));
    }
}
