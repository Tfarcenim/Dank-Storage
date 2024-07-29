package tfar.dankstorage.network.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import tfar.dankstorage.network.DankPacketHandler;
import tfar.dankstorage.platform.Services;
import tfar.dankstorage.utils.CommonUtils;

public class C2SSetFrequencyPacket implements C2SModPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, C2SSetFrequencyPacket> STREAM_CODEC =
            StreamCodec.ofMember(C2SSetFrequencyPacket::write, C2SSetFrequencyPacket::new);

    public static final CustomPacketPayload.Type<C2SSetFrequencyPacket> TYPE = new CustomPacketPayload.Type<>(
            DankPacketHandler.packet(C2SSetFrequencyPacket.class));

    private final int frequency;
    private final boolean set;

    public C2SSetFrequencyPacket(int frequency, boolean set) {
        this.frequency = frequency;
        this.set = set;
    }

    public C2SSetFrequencyPacket(FriendlyByteBuf buf) {
        frequency = buf.readInt();
        set = buf.readBoolean();
    }
    
    public void write(FriendlyByteBuf buf) {
        buf.writeInt(frequency);
        buf.writeBoolean(set);
    }

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

