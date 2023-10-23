package tfar.dankstorage.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import tfar.dankstorage.network.server.C2SButtonPacket;
import tfar.dankstorage.utils.ButtonAction;
import tfar.dankstorage.utils.CommonUtils;

import javax.annotation.Nullable;

@Mixin(Minecraft.class)
public class MinecraftClientMixin {

    @Shadow
    @Nullable
    public LocalPlayer player;

    @Shadow
    @Nullable
    public HitResult hitResult;

    @Inject(method = "pickBlock",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Inventory;findSlotMatchingItem(Lnet/minecraft/world/item/ItemStack;)I"),
            locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void dankPickBlock(CallbackInfo ci, boolean creative, BlockEntity blockEntity, ItemStack picked, HitResult.Type type, Inventory inventory) {
        if (CommonUtils.isHoldingDank(player) && hitResult != null && hitResult.getType() != HitResult.Type.MISS) {
            C2SButtonPacket.send(ButtonAction.PICK_BLOCK);
            ci.cancel();
        }
    }
}
