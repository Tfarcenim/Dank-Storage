package tfar.dankstorage.network;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import tfar.dankstorage.network.server.*;
import tfar.dankstorage.utils.PacketBufferEX;

import java.util.function.Function;

public class DankPacketHandlerFabric {

    public static void registerMessages() {
        ServerPlayNetworking.registerGlobalReceiver(PacketIds.scroll, new C2SMessageScrollSlot());
        ServerPlayNetworking.registerGlobalReceiver(PacketIds.lock_slot, new C2SMessageLockSlot());
        ServerPlayNetworking.registerGlobalReceiver(PacketIds.set_id, new C2SSetFrequencyPacket());
        ServerPlayNetworking.registerGlobalReceiver(PacketIds.request_contents,new C2SRequestContentsPacket());
        ServerPlayNetworking.registerGlobalReceiver(PacketIds.button_action,wrapC2S(C2SButtonPacket::new));
    }

    public static <MSG extends C2SModPacket> ServerPlayNetworking.PlayChannelHandler wrapC2S(Function<FriendlyByteBuf, MSG> decodeFunction) {
        return new ServerHandler<>(decodeFunction);
    }

    public static void sendList(ServerPlayer player, NonNullList<ItemStack> stacks) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        PacketBufferEX.writeList(buf, stacks);
        ServerPlayNetworking.send(player, PacketIds.sync_dank_inventory, buf);
    }
}
