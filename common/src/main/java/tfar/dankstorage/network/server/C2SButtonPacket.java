package tfar.dankstorage.network.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import tfar.dankstorage.item.DankItem;
import tfar.dankstorage.network.DankPacketHandler;
import tfar.dankstorage.platform.Services;
import tfar.dankstorage.utils.CommonUtils;
import tfar.dankstorage.utils.SerializationHelper;

public enum C2SButtonPacket implements C2SModPacket {
    PICK_BLOCK,
    TOGGLE_PICKUP, TOGGLE_USE_TYPE;
    public static final StreamCodec<RegistryFriendlyByteBuf, C2SButtonPacket> STREAM_CODEC =
            SerializationHelper.altEnumStreamCodec(C2SButtonPacket.class);


    public static final CustomPacketPayload.Type<C2SButtonPacket> TYPE = new CustomPacketPayload.Type<>(
            DankPacketHandler.packet(C2SButtonPacket.class));

    public static C2SButtonPacket fromNet(RegistryFriendlyByteBuf buf) {
        return buf.readEnum(C2SButtonPacket.class);
    }

    public void send() {
        Services.PLATFORM.sendToServer(this);
    }

    public void handleServer(ServerPlayer player) {
        switch (this) {
            case TOGGLE_PICKUP -> CommonUtils.togglePickupMode(player);
            case TOGGLE_USE_TYPE -> CommonUtils.toggleUseType(player);
            case PICK_BLOCK -> {
                HitResult hit = player.pick(5, 0, false);
                if (hit instanceof BlockHitResult blockHit && hit.getType() != HitResult.Type.MISS) {
                    ItemStack pick = Services.PLATFORM.getCloneStack(player.level(), blockHit.getBlockPos(),
                            player.level().getBlockState(blockHit.getBlockPos()), hit, player);
                    if (!pick.isEmpty()) {
                        if (player.getMainHandItem().getItem() instanceof DankItem)
                            CommonUtils.setPickSlot(player.level(), player.getMainHandItem(), pick);
                        else if (player.getOffhandItem().getItem() instanceof DankItem)
                            CommonUtils.setPickSlot(player.level(), player.getOffhandItem(), pick);
                    }
                }
            }
        }
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeEnum(this);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
