package tfar.dankstorage.network;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import tfar.dankstorage.DankStorage;
import tfar.dankstorage.network.client.*;
import tfar.dankstorage.network.server.*;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class DankPacketHandlerForge {

    public static SimpleChannel INSTANCE;
    public static void registerMessages() {

        INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(DankStorage.MODID, DankStorage.MODID), () -> "1.0", s -> true, s -> true);

        int i = 0;

        INSTANCE.registerMessage(i++,
                C2SScrollSlotPacket.class,
                C2SScrollSlotPacket::write,
                C2SScrollSlotPacket::new,
                wrapC2S());

        INSTANCE.registerMessage(i++,
                C2SLockSlotPacket.class,
                C2SLockSlotPacket::write,
                C2SLockSlotPacket::new,
                wrapC2S());



        INSTANCE.registerMessage(i++,
                C2SButtonPacket.class,
                C2SButtonPacket::write,
                C2SButtonPacket::new,
                wrapC2S());

        INSTANCE.registerMessage(i++,
                C2SSetFrequencyPacket.class,
                C2SSetFrequencyPacket::write,
                C2SSetFrequencyPacket::new,
                wrapC2S());

        INSTANCE.registerMessage(i++,
                C2SRequestContentsPacket.class,
                C2SRequestContentsPacket::write,
                C2SRequestContentsPacket::new,
                wrapC2S());

        ///////server to client

        INSTANCE.registerMessage(i++,
                S2CSendExtendedSlotChangePacket.class,
                S2CSendExtendedSlotChangePacket::write,
                S2CSendExtendedSlotChangePacket::new,
                wrapS2C());

        INSTANCE.registerMessage(i++,
                S2CSendGhostSlotPacket.class,
                S2CSendGhostSlotPacket::write,
                S2CSendGhostSlotPacket::new,
               wrapS2C());

        INSTANCE.registerMessage(i++,
                S2CSyncSelectedDankItemPacket.class,
                S2CSyncSelectedDankItemPacket::write,
                S2CSyncSelectedDankItemPacket::new,
                wrapS2C());

        INSTANCE.registerMessage(i++,
                S2CInitialSyncContainerPacket.class,
                S2CInitialSyncContainerPacket::write,
                S2CInitialSyncContainerPacket::new,
                wrapS2C());

        INSTANCE.registerMessage(i++,
                S2CContentsForDisplayPacket.class,
                S2CContentsForDisplayPacket::write,
                S2CContentsForDisplayPacket::new,
                wrapS2C());
    }

    private static <MSG extends S2CModPacket> BiConsumer<MSG, Supplier<NetworkEvent.Context>> wrapS2C() {
        return ((msg, contextSupplier) -> {
            contextSupplier.get().enqueueWork(msg::handleClient);
            contextSupplier.get().setPacketHandled(true);
        });
    }

    private static <MSG extends C2SModPacket> BiConsumer<MSG, Supplier<NetworkEvent.Context>> wrapC2S() {
        return ((msg, contextSupplier) -> {
            ServerPlayer player = contextSupplier.get().getSender();
            contextSupplier.get().enqueueWork(() -> msg.handleServer(player));
            contextSupplier.get().setPacketHandled(true);
        });
    }

    public static <MSG> void sendToClient(MSG packet, ServerPlayer player) {
        INSTANCE.sendTo(packet, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }

    public static <MSG> void sendToServer(MSG packet) {
        INSTANCE.sendToServer(packet);
    }
}
