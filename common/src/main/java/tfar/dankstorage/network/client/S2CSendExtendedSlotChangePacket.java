package tfar.dankstorage.network.client;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import tfar.dankstorage.menu.AbstractDankMenu;
import tfar.dankstorage.network.DankPacketHandler;
import tfar.dankstorage.utils.SerializationHelper;

import static tfar.dankstorage.client.CommonClient.getLocalPlayer;

public class S2CSendExtendedSlotChangePacket implements S2CModPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, S2CSendExtendedSlotChangePacket> STREAM_CODEC =
            StreamCodec.ofMember(S2CSendExtendedSlotChangePacket::write, S2CSendExtendedSlotChangePacket::new);


    public static final CustomPacketPayload.Type<S2CSendExtendedSlotChangePacket> TYPE = new CustomPacketPayload.Type<>(
            DankPacketHandler.packet(S2CSendExtendedSlotChangePacket.class));

    int windowId;
    int slot;
    ItemStack stack;

    public S2CSendExtendedSlotChangePacket(int windowId, int slot, ItemStack stack) {
        this.windowId = windowId;
        this.slot = slot;
        this.stack = stack;
    }

    public S2CSendExtendedSlotChangePacket(RegistryFriendlyByteBuf buf) {
        windowId = buf.readInt();
        slot = buf.readInt();
        stack = SerializationHelper.readExtendedItemStack(buf);
    }

    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeInt(windowId);
        buf.writeInt(slot);
        SerializationHelper.writeExtendedItemStack(buf, stack);
    }

    @Override
    public void handleClient() {
        Player player = getLocalPlayer();
        if (player != null && player.containerMenu instanceof AbstractDankMenu && windowId == player.containerMenu.containerId) {
            player.containerMenu.slots.get(slot).set(stack);
        }
    }

    @Override
    public Type<S2CSendExtendedSlotChangePacket> type() {
        return TYPE;
    }
}