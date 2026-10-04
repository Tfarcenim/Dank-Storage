package tfar.dankstorage.network.server;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import tfar.dankstorage.item.DankItem;
import tfar.dankstorage.network.DankPacketHandler;
import tfar.dankstorage.platform.Services;

public record C2SOpenMenuPacket(InteractionHand hand) implements C2SModPacket {

    public static final Type<C2SOpenMenuPacket> TYPE =new CustomPacketPayload.Type<>(DankPacketHandler.packet(C2SOpenMenuPacket.class));
    public static final StreamCodec<RegistryFriendlyByteBuf, C2SOpenMenuPacket> STREAM_CODEC =
            StreamCodec.composite(InteractionHand.STREAM_CODEC,C2SOpenMenuPacket::hand, C2SOpenMenuPacket::new);

    public static void send(InteractionHand hand) {
        Services.PLATFORM.sendToServer(new C2SOpenMenuPacket(hand));
    }

    public void handleServer(ServerPlayer player) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getItem() instanceof DankItem dankItem) {
            player.openMenu(dankItem.createProvider(stack));
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
