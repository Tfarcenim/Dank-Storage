package tfar.dankstorage.network.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import tfar.dankstorage.network.DankPacketHandlerForge;
import tfar.dankstorage.network.util.C2SPacketHelper;
import tfar.dankstorage.utils.CommonUtils;

public class C2SSetFrequencyPacket implements C2SPacketHelper {

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
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(frequency);
        buf.writeBoolean(set);
    }

    public static void send(int id, boolean set) {
        DankPacketHandlerForge.sendToServer(new C2SSetFrequencyPacket(id, set));
    }

    public void handleServer(ServerPlayer player) {
        CommonUtils.setTxtColor(player,frequency,set);
    }
}

