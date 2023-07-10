package tfar.dankstorage.network.server;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import tfar.dankstorage.DankStorage;
import tfar.dankstorage.network.DankPacketHandler;
import tfar.dankstorage.network.util.C2SPacketHelper;
import tfar.dankstorage.world.DankInventory;

public class C2SRequestContentsPacket implements C2SPacketHelper {

    private final int frequency;

    public C2SRequestContentsPacket(int frequency) {
        this.frequency = frequency;
    }

    public C2SRequestContentsPacket(FriendlyByteBuf buf) {
        frequency = buf.readInt();
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(frequency);
    }

    public static void send(int frequency) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeInt(frequency);
        DankPacketHandler.sendToServer(new C2SRequestContentsPacket(frequency));
    }

    public void handleServer(ServerPlayer player) {
        DankInventory dankInventory = DankStorage.instance.getData(frequency).createInventory(frequency);
            DankPacketHandler.sendContentsForDisplay(player,dankInventory.getContents());
    }
}

