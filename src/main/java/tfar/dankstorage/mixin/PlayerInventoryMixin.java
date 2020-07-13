package tfar.dankstorage.mixin;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tfar.dankstorage.event.MixinHooks;

@Mixin(PlayerInventory.class)
public class PlayerInventoryMixin {

	@Inject(method = "addItemStackToInventory",at = @At("HEAD"),cancellable = true)
	private void interceptItems(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
		if (MixinHooks.interceptItem((PlayerInventory)(Object)this,stack))cir.setReturnValue(true);
	}
}
