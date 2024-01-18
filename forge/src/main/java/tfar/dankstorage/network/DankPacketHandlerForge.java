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

    public static SimpleChannel SEND_BUTTON;

    public static void registerMessages() {

        INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(DankStorage.MODID, DankStorage.MODID), () -> "1.0", s -> true, s -> true);

        int i = 0;

        INSTANCE.registerMessage(i++,
                C2SScrollSlotPacket.class,
                C2SScrollSlotPacket::encode,
                C2SScrollSlotPacket::new,
                C2SScrollSlotPacket::handle);

        INSTANCE.registerMessage(i++,
                C2SLockSlotPacket.class,
                C2SLockSlotPacket::encode,
                C2SLockSlotPacket::new,
                C2SLockSlotPacket::handle);



        INSTANCE.registerMessage(i++,
                C2SButtonPacket.class,
                C2SButtonPacket::encode,
                C2SButtonPacket::new,
                C2SButtonPacket::handle);

        INSTANCE.registerMessage(i++,
                C2SMessagePickBlock.class,
                C2SMessagePickBlock::encode,
                C2SMessagePickBlock::new,
                C2SMessagePickBlock::handle);

        INSTANCE.registerMessage(i++,
                C2SSetFrequencyPacket.class,
                C2SSetFrequencyPacket::encode,
                C2SSetFrequencyPacket::new,
                C2SSetFrequencyPacket::handle);

        INSTANCE.registerMessage(i++,
                C2SRequestContentsPacket.class,
                C2SRequestContentsPacket::encode,
                C2SRequestContentsPacket::new,
                C2SRequestContentsPacket::handle);

        ///////

        INSTANCE.registerMessage(i++,
                S2CSendCustomSlotChangePacket.class,
                S2CSendCustomSlotChangePacket::encode,
                S2CSendCustomSlotChangePacket::new,
                S2CSendCustomSlotChangePacket::handle);

        INSTANCE.registerMessage(i++,
                S2CSendGhostSlotPacket.class,
                S2CSendGhostSlotPacket::write,
                S2CSendGhostSlotPacket::new,
               wrapS2C());

        INSTANCE.registerMessage(i++,
                S2CSyncSelectedItemPacket.class,
                S2CSyncSelectedItemPacket::encode,
                S2CSyncSelectedItemPacket::new,
                S2CSyncSelectedItemPacket::handle);

        INSTANCE.registerMessage(i++,
                S2CCustomSyncDataPacket.class,
                S2CCustomSyncDataPacket::encode,
                S2CCustomSyncDataPacket::new,
                S2CCustomSyncDataPacket::handle);

        INSTANCE.registerMessage(i++,
                S2CContentsForDisplayPacket.class,
                S2CContentsForDisplayPacket::encode,
                S2CContentsForDisplayPacket::new,
                S2CContentsForDisplayPacket::handle);
    }

    private static <MSG extends S2CModPacket> BiConsumer<MSG, Supplier<NetworkEvent.Context>> wrapS2C() {
        return ((msg, contextSupplier) -> {
            contextSupplier.get().enqueueWork(msg::handleClient);
            contextSupplier.get().setPacketHandled(true);
        });
    }

    public static void sendCustomSlotChange(ServerPlayer player, int id, int slot, ItemStack stack) {
        sendToClient(new S2CSendCustomSlotChangePacket(id,slot,stack),player);
    }


    public static void sendCustomSyncData(ServerPlayer player, int stateID, int containerID, NonNullList<ItemStack> stacks, ItemStack carried) {
        sendToClient(new S2CCustomSyncDataPacket(stateID,containerID,stacks,carried),player);
    }

    public static void sendSelectedItem(ServerPlayer player, ItemStack stack) {
        sendToClient(new S2CSyncSelectedItemPacket(stack),player);
    }

    public static void sendContentsForDisplay(ServerPlayer player, NonNullList<ItemStack> stacks) {
        sendToClient(new S2CContentsForDisplayPacket(stacks),player);
    }

    public static <MSG> void sendToClient(MSG packet, ServerPlayer player) {
        INSTANCE.sendTo(packet, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }

    public static <MSG> void sendToServer(MSG packet) {
        INSTANCE.sendToServer(packet);
    }
}
