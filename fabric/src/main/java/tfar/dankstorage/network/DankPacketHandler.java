package tfar.dankstorage.network;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import tfar.dankstorage.DankStorageFabric;
import tfar.dankstorage.network.server.*;

import java.util.List;

public class DankPacketHandler {

    public static final ResourceLocation toggle_pickup = new ResourceLocation(DankStorageFabric.MODID, "toggle_pickup");
    public static final ResourceLocation tag_mode = new ResourceLocation(DankStorageFabric.MODID, "tag_mode");
    public static final ResourceLocation sort = new ResourceLocation(DankStorageFabric.MODID, "sort");
    public static final ResourceLocation lock_slot = new ResourceLocation(DankStorageFabric.MODID, "lock_slot");

    public static final ResourceLocation pick_block = new ResourceLocation(DankStorageFabric.MODID, "pick_block");
    public static final ResourceLocation toggle_use = new ResourceLocation(DankStorageFabric.MODID, "toggle_use");
    public static final ResourceLocation scroll = new ResourceLocation(DankStorageFabric.MODID, "scroll");
    public static final ResourceLocation set_id = new ResourceLocation(DankStorageFabric.MODID, "set_id");
    public static final ResourceLocation lock_id = new ResourceLocation(DankStorageFabric.MODID, "lock_id");
    public static final ResourceLocation request_contents = new ResourceLocation(DankStorageFabric.MODID, "request_contents");

    public static final ResourceLocation sync_slot = new ResourceLocation(DankStorageFabric.MODID, "sync_slot");
    public static final ResourceLocation sync_ghost = new ResourceLocation(DankStorageFabric.MODID, "sync_ghost");
    public static final ResourceLocation sync_container = new ResourceLocation(DankStorageFabric.MODID, "sync_container");
    public static final ResourceLocation sync_data = new ResourceLocation(DankStorageFabric.MODID, "sync_data");
    public static final ResourceLocation sync_inventory = new ResourceLocation(DankStorageFabric.MODID, "sync_inventory");
    public static final ResourceLocation compress = new ResourceLocation(DankStorageFabric.MODID, "compress");

    public static void registerMessages() {
        ServerPlayNetworking.registerGlobalReceiver(scroll, new C2SMessageScrollSlot());
        ServerPlayNetworking.registerGlobalReceiver(lock_slot, new C2SMessageLockSlot());
        ServerPlayNetworking.registerGlobalReceiver(sort, new C2SMessageSort());
        ServerPlayNetworking.registerGlobalReceiver(tag_mode, new C2SMessageTagMode());
        ServerPlayNetworking.registerGlobalReceiver(toggle_pickup, new C2SMessageTogglePickup());
        ServerPlayNetworking.registerGlobalReceiver(toggle_use, new C2SMessageToggleUseType());
        ServerPlayNetworking.registerGlobalReceiver(pick_block, new C2SMessagePickBlock());
        ServerPlayNetworking.registerGlobalReceiver(set_id, new C2SSetFrequencyPacket());
        ServerPlayNetworking.registerGlobalReceiver(lock_id,new C2SMessageLockFrequency());
        ServerPlayNetworking.registerGlobalReceiver(request_contents,new C2SRequestContentsPacket());
        ServerPlayNetworking.registerGlobalReceiver(compress,new C2SMessageCompress());
    }

    public static void sendSyncSlot(ServerPlayer player, int id, int slot, ItemStack stack) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeInt(id);
        buf.writeInt(slot);
        PacketBufferEX.writeExtendedItemStack(buf, stack);
        ServerPlayNetworking.send(player, DankPacketHandler.sync_slot, buf);
    }

    public static void sendGhostItem(ServerPlayer player, int id, int slot, ItemStack stack) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeInt(id);
        buf.writeInt(slot);
        PacketBufferEX.writeExtendedItemStack(buf, stack);
        ServerPlayNetworking.send(player, DankPacketHandler.sync_ghost, buf);
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

        ServerPlayNetworking.send(player, DankPacketHandler.sync_container, buf);
    }

    public static void sendSelectedItem(ServerPlayer player, ItemStack stack) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        PacketBufferEX.writeExtendedItemStack(buf, stack);
        ServerPlayNetworking.send(player, DankPacketHandler.sync_data, buf);
    }

    public static void sendList(ServerPlayer player, List<ItemStack> stacks) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        PacketBufferEX.writeList(buf, stacks);
        ServerPlayNetworking.send(player, DankPacketHandler.sync_inventory, buf);
    }
}
