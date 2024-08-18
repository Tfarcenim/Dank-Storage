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
import tfar.dankstorage.utils.KeybindAction;
import tfar.dankstorage.utils.CommonUtils;
import tfar.dankstorage.utils.SerializationHelper;

public record C2SButtonPacket(KeybindAction keybindAction) implements C2SModPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, C2SButtonPacket> STREAM_CODEC =
            StreamCodec.composite(SerializationHelper.enumCodec(KeybindAction.class), C2SButtonPacket::keybindAction, C2SButtonPacket::new);


    public static final CustomPacketPayload.Type<C2SButtonPacket> TYPE = new CustomPacketPayload.Type<>(
            DankPacketHandler.packet(C2SButtonPacket.class));


    public static void send(KeybindAction keybindAction) {
        Services.PLATFORM.sendToServer(new C2SButtonPacket(keybindAction));
    }

    public void handleServer(ServerPlayer player) {
        switch (keybindAction) {
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
        buf.writeInt(keybindAction.ordinal());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
