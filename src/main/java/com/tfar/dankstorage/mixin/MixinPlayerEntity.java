package com.tfar.dankstorage.mixin;

import com.tfar.dankstorage.block.DankItemBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShootableItem;
import net.minecraftforge.items.CapabilityItemHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.IntStream;

@Mixin(PlayerEntity.class)
public class MixinPlayerEntity {
  @Inject(method = "findAmmo", at = @At("HEAD"), cancellable = true)
  private void findAmmo(ItemStack shootable, CallbackInfoReturnable<ItemStack> cir) {
    ItemStack ammo = myFindAmmo(shootable);
    if (!ammo.isEmpty()) {
      cir.setReturnValue(ammo);
      cir.cancel();
    }
  }

  private ItemStack myFindAmmo(ItemStack shootable) {
    if (!(shootable.getItem() instanceof DankItemBlock)) return ItemStack.EMPTY;
    Predicate<ItemStack> predicate = ((ShootableItem) shootable.getItem()).getInventoryAmmoPredicate();
    return shootable.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            .map(iItemHandler -> IntStream.range(0, iItemHandler.getSlots())
                    .mapToObj(iItemHandler::getStackInSlot)
                    .filter(predicate).findFirst()).orElse(Optional.of(ItemStack.EMPTY)).orElse(ItemStack.EMPTY);
  }
}
