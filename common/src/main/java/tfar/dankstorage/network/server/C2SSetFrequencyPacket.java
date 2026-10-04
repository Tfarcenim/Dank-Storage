package tfar.dankstorage.network.server;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import tfar.dankstorage.network.DankPacketHandler;
import tfar.dankstorage.platform.Services;
import tfar.dankstorage.utils.CommonUtils;

public record C2SSetFrequencyPacket(int frequency,boolean set) implements C2SModPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, C2SSetFrequencyPacket> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.INT,C2SSetFrequencyPacket::frequency,
                    ByteBufCodecs.BOOL,C2SSetFrequencyPacket::set, C2SSetFrequencyPacket::new);

    public static final CustomPacketPayload.Type<C2SSetFrequencyPacket> TYPE = new CustomPacketPayload.Type<>(
            DankPacketHandler.packet(C2SSetFrequencyPacket.class));

    public static void send(int id, boolean set) {
        Services.PLATFORM.sendToServer(new C2SSetFrequencyPacket(id, set));
    }

    public void handleServer(ServerPlayer player) {
        CommonUtils.setTxtColor(player,frequency,set);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

