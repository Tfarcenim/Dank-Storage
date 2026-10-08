package tfar.dankstorage.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import tfar.dankstorage.network.server.C2SButtonPacket;
import tfar.dankstorage.utils.KeybindAction;
import tfar.dankstorage.utils.CommonUtils;


@Mixin(Minecraft.class)
public class MinecraftClientMixin {

    @Shadow
    @Nullable
    public LocalPlayer player;

    @Shadow
    @Nullable
    public HitResult hitResult;

    @Inject(method = "pickBlockOrEntity",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;handlePickItemFromBlock(Lnet/minecraft/core/BlockPos;Z)V"),
            locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void dankPickBlock(CallbackInfo ci, boolean includeData) {
        if (CommonUtils.isHoldingDank(player) && hitResult != null && hitResult.getType() != HitResult.Type.MISS) {
            C2SButtonPacket.PICK_BLOCK.send();
            ci.cancel();
        }
    }
}
