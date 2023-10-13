package tfar.dankstorage.mixin;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfar.dankstorage.item.CoDankItem;

@Mixin(ItemEntity.class)
public class ItemEntityMixin {

    @Shadow
    private int age;

    @Shadow @Final private static int INFINITE_LIFETIME;

    @Inject(method = "<init>(Lnet/minecraft/world/level/Level;DDDLnet/minecraft/world/item/ItemStack;)V", at = @At("RETURN"))
    private void noDespawn(Level world, double x, double y, double z, ItemStack stack, CallbackInfo ci) {
        if (stack.getItem() instanceof CoDankItem) {
            this.age = INFINITE_LIFETIME;
        }
    }
}
