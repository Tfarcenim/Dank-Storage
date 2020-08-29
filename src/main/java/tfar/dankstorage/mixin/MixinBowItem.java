package tfar.dankstorage.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfar.dankstorage.event.MixinEvents;

@Mixin(BowItem.class)
public class MixinBowItem {
  @Inject(method = "onPlayerStoppedUsing",at = @At("TAIL"))
  private void onPlayerStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft, CallbackInfo callbackInfo){
    MixinEvents.shrinkAmmo(stack, worldIn, entityLiving, timeLeft);
  }
}
