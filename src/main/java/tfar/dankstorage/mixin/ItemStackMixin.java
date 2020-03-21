package tfar.dankstorage.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfar.dankstorage.DankItemBlock;
import tfar.dankstorage.utils.MixinHooks;
import tfar.dankstorage.utils.Utils;

import java.util.function.Consumer;

@Mixin(ItemStack.class)
public class ItemStackMixin {
	@Inject(method = "damageItem",at = @At(value = "INVOKE",target = "Lnet/minecraft/item/ItemStack;shrink(I)V",shift = At.Shift.AFTER))
	private <T extends LivingEntity> void actuallyBreakItem(int p_222118_1_, T livingEntity, Consumer<T> p_222118_3_, CallbackInfo ci){
		MixinHooks.actuallyBreakItem(p_222118_1_,livingEntity,p_222118_3_,ci);
	}
}
