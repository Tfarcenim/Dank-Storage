package tfar.dankstorage.network.server;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import tfar.dankstorage.inventory.DankInventory;
import tfar.dankstorage.network.DankPacketHandler;
import tfar.dankstorage.network.client.S2CContentsForDisplayPacket;
import tfar.dankstorage.platform.Services;
import tfar.dankstorage.world.DankSavedDatas;

public record C2SRequestContentsPacket(int frequency) implements C2SModPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, C2SRequestContentsPacket> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.INT,C2SRequestContentsPacket::frequency, C2SRequestContentsPacket::new);

    public static final Type<C2SRequestContentsPacket> TYPE = new Type<>(
            DankPacketHandler.packet(C2SRequestContentsPacket.class));

    public static void send(int frequency) {
        Services.PLATFORM.sendToServer(new C2SRequestContentsPacket(frequency));
    }

    public void handleServer(ServerPlayer player) {
        DankInventory dankInventoryForge = DankSavedDatas.get(player.level().getServer()).get(frequency).getOrCreateInventory(player.registryAccess());
        Services.PLATFORM.sendToClient(new S2CContentsForDisplayPacket(dankInventoryForge.getContents()), player);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

