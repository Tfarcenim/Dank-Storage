package tfar.dankstorage.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShootableItem;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfar.dankstorage.DankItemBlock;
import tfar.dankstorage.mixinhelpers.UseDankStorage;

import java.util.function.Predicate;
import java.util.stream.IntStream;

@Mixin(BowItem.class)
public class MixinBowItem {
  @Inject(method = "onPlayerStoppedUsing",at = @At("TAIL"))
  private void onPlayerStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft, CallbackInfo callbackInfo){
    a(stack, worldIn, entityLiving, timeLeft);
  }

  private void a(ItemStack bow, World worldIn, LivingEntity entityLiving, int timeLeft){
    if (entityLiving instanceof PlayerEntity && !worldIn.isRemote){
      PlayerEntity player = (PlayerEntity) entityLiving;
      Predicate<ItemStack> predicate = ((ShootableItem) bow.getItem()).getInventoryAmmoPredicate();
      if (((UseDankStorage)player).useDankStorage() && !player.abilities.isCreativeMode){
        ItemStack dank = getDankStorage(player);
        dank.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(iItemHandler -> {
          for (int i = 0;i < iItemHandler.getSlots();i++){
            ItemStack stack = iItemHandler.getStackInSlot(i);
            if (predicate.test(stack)){
              iItemHandler.extractItem(i,1,false);
              break;
            }
          }
        });
      }
    }
  }

  private ItemStack getDankStorage(PlayerEntity player){
    return IntStream.range(0, player.inventory.getSizeInventory()).mapToObj(i -> player.inventory.getStackInSlot(i)).filter(stack -> stack.getItem() instanceof DankItemBlock).findFirst().orElse(ItemStack.EMPTY);
  }

}
