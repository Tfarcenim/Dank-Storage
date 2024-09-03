package tfar.dankstorage.network.client;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import tfar.dankstorage.network.DankPacketHandler;
import tfar.dankstorage.utils.SerializationHelper;
import tfar.dankstorage.world.ClientData;

import static tfar.dankstorage.client.CommonClient.getLocalPlayer;

public class S2CSyncSelectedDankItemPacket implements S2CModPacket {
    public static final StreamCodec<RegistryFriendlyByteBuf, S2CSyncSelectedDankItemPacket> STREAM_CODEC =
            StreamCodec.ofMember(S2CSyncSelectedDankItemPacket::write, S2CSyncSelectedDankItemPacket::new);


    public static final CustomPacketPayload.Type<S2CSyncSelectedDankItemPacket> TYPE = new CustomPacketPayload.Type<>(
            DankPacketHandler.packet(S2CSyncSelectedDankItemPacket.class));

    private final ItemStack stack;

    public S2CSyncSelectedDankItemPacket(ItemStack stack) {
        this.stack = stack;
    }

    public S2CSyncSelectedDankItemPacket(RegistryFriendlyByteBuf buf) {
        stack = SerializationHelper.readExtendedItemStack(buf);
    }

    @Override
    public void handleClient() {
        Player player = getLocalPlayer();
        if (player != null) {
        }
    }

    public void write(RegistryFriendlyByteBuf buf) {
        SerializationHelper.writeExtendedItemStack(buf,stack);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}