package tfar.dankstorage.mixin;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tfar.dankstorage.item.DankItem;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Shadow public abstract Item getItem();

    @Inject(method = "canBeHurtBy",at = @At("RETURN"),cancellable = true)
    private void blockNonVoid(DamageSource pDamageSource, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue()) return;
        if (getItem() instanceof DankItem && !pDamageSource.is(DamageTypes.FELL_OUT_OF_WORLD)) {
            cir.setReturnValue(false);
        }
    }
}
