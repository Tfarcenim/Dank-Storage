package tfar.dankstorage.network.server;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import tfar.dankstorage.item.DankItem;
import tfar.dankstorage.network.DankPacketHandler;
import tfar.dankstorage.platform.Services;

public record C2SScrollSlotPacket(boolean right) implements C2SModPacket {
    public static final StreamCodec<RegistryFriendlyByteBuf, C2SScrollSlotPacket> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.BOOL,C2SScrollSlotPacket::right, C2SScrollSlotPacket::new);

    public static final Type<C2SScrollSlotPacket> TYPE = new Type<>(
            DankPacketHandler.packet(C2SScrollSlotPacket.class));


    public static void send(boolean right) {
        Services.PLATFORM.sendToServer(new C2SScrollSlotPacket(right));
    }

    public void handleServer(ServerPlayer player) {
        if (player.getMainHandItem().getItem() instanceof DankItem)
            DankItem.changeSelectedItem(player.getMainHandItem(), right, player);
        else if (player.getOffhandItem().getItem() instanceof DankItem)
            DankItem.changeSelectedItem(player.getOffhandItem(), right, player);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

