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
import tfar.dankstorage.event.ClientMixinEvents;
import tfar.dankstorage.network.server.C2SMessagePickBlock;
import tfar.dankstorage.utils.Utils;

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
        if (Utils.isHoldingDank(player) && hitResult != null && hitResult.getType() != HitResult.Type.MISS) {
            C2SMessagePickBlock.send(picked);
            ci.cancel();
        }
    }
}
