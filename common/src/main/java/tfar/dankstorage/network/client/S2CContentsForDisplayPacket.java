package tfar.dankstorage.network.client;

import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;
import tfar.dankstorage.network.DankPacketHandler;
import tfar.dankstorage.utils.PacketBufferEX;
import tfar.dankstorage.world.ClientData;

import java.util.List;

public class S2CContentsForDisplayPacket implements S2CModPacket {


    public static final StreamCodec<RegistryFriendlyByteBuf, S2CContentsForDisplayPacket> STREAM_CODEC =
            StreamCodec.ofMember(S2CContentsForDisplayPacket::write, S2CContentsForDisplayPacket::new);


    public static final CustomPacketPayload.Type<S2CContentsForDisplayPacket> TYPE = new CustomPacketPayload.Type<>(
            DankPacketHandler.packet(S2CContentsForDisplayPacket.class));

    private final List<ItemStack> stacks;

    public S2CContentsForDisplayPacket(NonNullList<ItemStack> stacks) {
        this.stacks = stacks;
    }

    public S2CContentsForDisplayPacket(RegistryFriendlyByteBuf buf) {
        stacks = PacketBufferEX.readList(buf);
    }

    @Override
    public void handleClient() {
            ClientData.setList(stacks);
    }

    public void write(RegistryFriendlyByteBuf buf) {
        ItemStack.OPTIONAL_LIST_STREAM_CODEC.encode(buf,stacks);
        PacketBufferEX.writeList(buf, stacks);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}