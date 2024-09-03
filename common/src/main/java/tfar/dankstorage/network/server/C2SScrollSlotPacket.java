package tfar.dankstorage.network.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import tfar.dankstorage.item.DankItem;
import tfar.dankstorage.network.DankPacketHandler;
import tfar.dankstorage.platform.Services;
import tfar.dankstorage.utils.CommonUtils;

public class C2SScrollSlotPacket implements C2SModPacket {
    public static final StreamCodec<RegistryFriendlyByteBuf, C2SScrollSlotPacket> STREAM_CODEC =
            StreamCodec.ofMember(C2SScrollSlotPacket::write, C2SScrollSlotPacket::new);

    public static final CustomPacketPayload.Type<C2SScrollSlotPacket> TYPE = new CustomPacketPayload.Type<>(
            DankPacketHandler.packet(C2SScrollSlotPacket.class));


    private final boolean right;

    public C2SScrollSlotPacket(boolean right) {
        this.right = right;
    }

    public C2SScrollSlotPacket(FriendlyByteBuf buf) {
        right = buf.readBoolean();
    }

    public static void send(boolean right) {
        Services.PLATFORM.sendToServer(new C2SScrollSlotPacket(right));
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeBoolean(right);
    }

    public void handleServer(ServerPlayer player) {
        if (player.getMainHandItem().getItem() instanceof DankItem)
            DankItem.changeSelectedItem(player.getMainHandItem(), right,player);
        else if (player.getOffhandItem().getItem() instanceof DankItem)
            DankItem.changeSelectedItem(player.getOffhandItem(), right,player);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

