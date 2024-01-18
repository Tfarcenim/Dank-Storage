package tfar.dankstorage.network;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import tfar.dankstorage.network.server.*;
import tfar.dankstorage.utils.PacketBufferEX;

public class DankPacketHandlerFabric {

    public static void registerMessages() {
        ServerPlayNetworking.registerGlobalReceiver(PacketIds.scroll, new C2SMessageScrollSlot());
        ServerPlayNetworking.registerGlobalReceiver(PacketIds.lock_slot, new C2SMessageLockSlot());
        ServerPlayNetworking.registerGlobalReceiver(PacketIds.set_id, new C2SSetFrequencyPacket());
        ServerPlayNetworking.registerGlobalReceiver(PacketIds.request_contents,new C2SRequestContentsPacket());
        ServerPlayNetworking.registerGlobalReceiver(PacketIds.button_action,new C2SButtonPacket());
    }

    public static void sendSyncContainer(ServerPlayer player,int stateID, int containerID, NonNullList<ItemStack> stacks,ItemStack carried) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeInt(stateID);
        buf.writeInt(containerID);

        buf.writeItem(carried);

        buf.writeShort(stacks.size());

        for (ItemStack stack : stacks) {
            PacketBufferEX.writeExtendedItemStack(buf, stack);
        }

        ServerPlayNetworking.send(player, PacketIds.initial_sync_container, buf);
    }

    public static void sendList(ServerPlayer player, NonNullList<ItemStack> stacks) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        PacketBufferEX.writeList(buf, stacks);
        ServerPlayNetworking.send(player, PacketIds.sync_dank_inventory, buf);
    }
}
