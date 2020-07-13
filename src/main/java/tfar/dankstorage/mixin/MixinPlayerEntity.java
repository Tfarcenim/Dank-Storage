package tfar.dankstorage.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShootableItem;
import net.minecraftforge.items.CapabilityItemHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tfar.dankstorage.ducks.UseDankStorage;
import tfar.dankstorage.DankItem;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.IntStream;

@Mixin(PlayerEntity.class)
public class MixinPlayerEntity implements UseDankStorage{
  @Shadow @Final public PlayerInventory inventory;

  public boolean useDankStorage = false;

  @Inject(method = "findAmmo", at = @At("HEAD"), cancellable = true)
  private void findAmmo(ItemStack shootable, CallbackInfoReturnable<ItemStack> cir) {
    ItemStack ammo = myFindAmmo(shootable);
    useDankStorage = !ammo.isEmpty();
    if (!ammo.isEmpty()) {
      cir.setReturnValue(ammo);
      cir.cancel();
    }
  }

  private ItemStack myFindAmmo(ItemStack bow) {
    Predicate<ItemStack> predicate = ((ShootableItem) bow.getItem()).getInventoryAmmoPredicate();

    ItemStack dank = getDankStorage(bow);

    return dank.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            .map(iItemHandler -> IntStream.range(0, iItemHandler.getSlots())
                    .mapToObj(iItemHandler::getStackInSlot)
                    .filter(predicate).findFirst()).orElse(Optional.of(ItemStack.EMPTY)).orElse(ItemStack.EMPTY);
  }

  private ItemStack getDankStorage(ItemStack bow){
    return IntStream.range(0, this.inventory.getSizeInventory()).mapToObj(i -> this.inventory.getStackInSlot(i)).filter(stack -> stack.getItem() instanceof DankItem).findFirst().orElse(ItemStack.EMPTY);
  }

  @Override
  public boolean useDankStorage() {
    return useDankStorage;
  }
}
