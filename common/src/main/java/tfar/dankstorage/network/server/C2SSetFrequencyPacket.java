package tfar.dankstorage.network.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import tfar.dankstorage.platform.Services;
import tfar.dankstorage.utils.CommonUtils;

public class C2SSetFrequencyPacket implements C2SModPacket {

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

    @Override
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
}

