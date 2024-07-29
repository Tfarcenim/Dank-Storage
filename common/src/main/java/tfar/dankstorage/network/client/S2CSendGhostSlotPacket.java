package tfar.dankstorage.network.client;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import tfar.dankstorage.client.CommonClient;
import tfar.dankstorage.menu.AbstractDankMenu;
import tfar.dankstorage.network.DankPacketHandler;
import tfar.dankstorage.utils.PacketBufferEX;

public class S2CSendGhostSlotPacket implements S2CModPacket {
    public static final StreamCodec<RegistryFriendlyByteBuf, S2CSendGhostSlotPacket> STREAM_CODEC =
            StreamCodec.ofMember(S2CSendGhostSlotPacket::write, S2CSendGhostSlotPacket::new);


    public static final CustomPacketPayload.Type<S2CSendGhostSlotPacket> TYPE = new CustomPacketPayload.Type<>(
            DankPacketHandler.packet(S2CSendGhostSlotPacket.class));

    int windowId;
    int slot;
    ItemStack stack;

    public S2CSendGhostSlotPacket(int windowId, int slot, ItemStack stack) {
        this.windowId = windowId;
        this.slot = slot;
        this.stack = stack;
    }

    public S2CSendGhostSlotPacket(RegistryFriendlyByteBuf buf) {
        windowId = buf.readInt();
        slot = buf.readInt();
        stack = PacketBufferEX.readExtendedItemStack(buf);
    }

    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeInt(windowId);
        buf.writeInt(slot);
        PacketBufferEX.writeExtendedItemStack(buf, stack);
    }

    @Override
    public void handleClient() {
        Player player = CommonClient.getLocalPlayer();
        if (player != null && player.containerMenu instanceof AbstractDankMenu dankMenu && windowId == player.containerMenu.containerId) {
            dankMenu.dankInventory.setGhostItem(slot,stack.getItem());
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}