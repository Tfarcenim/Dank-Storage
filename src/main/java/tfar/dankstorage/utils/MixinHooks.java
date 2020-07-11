package tfar.dankstorage.utils;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfar.dankstorage.DankItem;

import java.util.function.Consumer;

public class MixinHooks {
	public static <T extends LivingEntity> void actuallyBreakItem(int p_222118_1_, T livingEntity, Consumer<T> p_222118_3_, CallbackInfo ci) {
		ItemStack actualStack = livingEntity.getHeldItemMainhand();
		if (actualStack.getItem() instanceof DankItem && Utils.isConstruction(actualStack)) {
			Utils.getHandler(actualStack).extractItem(Utils.getSelectedSlot(actualStack),1,false);
		}
	}
}
