package tfar.dankstorage.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tfar.dankstorage.ducks.UseDankStorage;
import tfar.dankstorage.event.MixinEvents;

@Mixin(PlayerEntity.class)
public class MixinPlayerEntity implements UseDankStorage {

  public boolean useDankStorage = false;

  @Inject(method = "findAmmo", at = @At("HEAD"), cancellable = true)
  private void findAmmo(ItemStack shootable, CallbackInfoReturnable<ItemStack> cir) {
    ItemStack ammo = MixinEvents.findAmmo((PlayerEntity)(Object)this,shootable);
    useDankStorage = !ammo.isEmpty();
    if (useDankStorage) {
      cir.setReturnValue(ammo);
    }
  }

  @Override
  public boolean useDankStorage() {
    return useDankStorage;
  }
}
