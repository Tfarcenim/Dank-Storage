package tfar.dankstorage.mixin;

import net.minecraft.world.item.crafting.RecipeManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfar.dankstorage.utils.CommonUtils;

@Mixin(RecipeManager.class)
public class RecipeManagerMixin {

    @Inject(method = "apply", at = @At("RETURN"))
    public void uncacheCompressionRecipes(CallbackInfo ci) {
        CommonUtils.uncacheRecipes();
    }
}
