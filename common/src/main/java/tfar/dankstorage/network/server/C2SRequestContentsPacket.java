package tfar.dankstorage.network.server;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import tfar.dankstorage.DankStorage;
import tfar.dankstorage.inventory.DankInterface;
import tfar.dankstorage.network.client.S2CContentsForDisplayPacket;
import tfar.dankstorage.platform.Services;

public class C2SRequestContentsPacket implements C2SModPacket {

    private final int frequency;

    public C2SRequestContentsPacket(int frequency) {
        this.frequency = frequency;
    }

    public C2SRequestContentsPacket(FriendlyByteBuf buf) {
        frequency = buf.readInt();
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeInt(frequency);
    }

    public static void send(int frequency) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeInt(frequency);
        Services.PLATFORM.sendToServer(new C2SRequestContentsPacket(frequency));
    }

    public void handleServer(ServerPlayer player) {
        DankInterface dankInventoryForge = DankStorage.getData(frequency,player.server).createInventory(frequency);
            Services.PLATFORM.sendToClient(new S2CContentsForDisplayPacket(dankInventoryForge.getContents()), player);
    }
}

