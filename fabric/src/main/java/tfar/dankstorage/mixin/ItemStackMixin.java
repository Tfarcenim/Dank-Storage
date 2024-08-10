package tfar.dankstorage.mixin;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class ItemStackMixin {
    @Inject(method = "canBeHurtBy",at = @At("RETURN"),cancellable = true)
    private void blockNonVoid(DamageSource pDamageSource, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue()) return;

    }
}
