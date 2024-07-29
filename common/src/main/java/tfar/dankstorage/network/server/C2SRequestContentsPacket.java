package tfar.dankstorage.network.server;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import tfar.dankstorage.DankStorage;
import tfar.dankstorage.inventory.DankInterface;
import tfar.dankstorage.network.DankPacketHandler;
import tfar.dankstorage.network.client.S2CContentsForDisplayPacket;
import tfar.dankstorage.platform.Services;

public class C2SRequestContentsPacket implements C2SModPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, C2SRequestContentsPacket> STREAM_CODEC =
            StreamCodec.ofMember(C2SRequestContentsPacket::write, C2SRequestContentsPacket::new);

    public static final CustomPacketPayload.Type<C2SRequestContentsPacket> TYPE = new CustomPacketPayload.Type<>(
            DankPacketHandler.packet(C2SRequestContentsPacket.class));

    private final int frequency;

    public C2SRequestContentsPacket(int frequency) {
        this.frequency = frequency;
    }

    public C2SRequestContentsPacket(FriendlyByteBuf buf) {
        frequency = buf.readInt();
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeInt(frequency);
    }

    public static void send(int frequency) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeInt(frequency);
        Services.PLATFORM.sendToServer(new C2SRequestContentsPacket(frequency));
    }

    public void handleServer(ServerPlayer player) {
        DankInterface dankInventoryForge = DankStorage.getData(frequency,player.server).createInventory(player.level().registryAccess(),frequency);
            Services.PLATFORM.sendToClient(new S2CContentsForDisplayPacket(dankInventoryForge.getContents()), player);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

