package tfar.dankstorage.network.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import tfar.dankstorage.item.CDankItem;
import tfar.dankstorage.platform.Services;
import tfar.dankstorage.utils.KeybindAction;
import tfar.dankstorage.utils.CommonUtils;

public class C2SButtonPacket implements C2SModPacket {

    private final KeybindAction keybindAction;

    public C2SButtonPacket(KeybindAction keybindAction) {
        this.keybindAction = keybindAction;
    }

    public C2SButtonPacket(FriendlyByteBuf buf) {
        keybindAction = KeybindAction.values()[buf.readInt()];
    }

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
                        if (player.getMainHandItem().getItem() instanceof CDankItem)
                            CommonUtils.setPickSlot(player.level(), player.getMainHandItem(), pick);
                        else if (player.getOffhandItem().getItem() instanceof CDankItem)
                            CommonUtils.setPickSlot(player.level(), player.getOffhandItem(), pick);
                    }
                }
            }
        }
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeInt(keybindAction.ordinal());
    }

}
