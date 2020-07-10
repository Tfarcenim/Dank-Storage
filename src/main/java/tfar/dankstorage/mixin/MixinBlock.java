package tfar.dankstorage.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tfar.dankstorage.utils.Utils;

import java.util.List;

@Mixin(Block.class)
public class MixinBlock {
  @Inject(method = "getDrops(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/server/ServerWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/tileentity/TileEntity;Lnet/minecraft/entity/Entity;Lnet/minecraft/item/ItemStack;)Ljava/util/List;",at = @At("HEAD"),cancellable = true)
  private static void hijacklootcontextbuilder(BlockState state, ServerWorld worldIn,
                                       BlockPos pos, TileEntity tileEntityIn,
                                       Entity entityIn, ItemStack tool,
                                       CallbackInfoReturnable<List<ItemStack>> drops){
    if (Utils.isConstruction(tool)) {
      drops.setReturnValue(newlootcontext(state, worldIn, pos, tileEntityIn, entityIn, tool));
      drops.cancel();
    }
  }
  private static List<ItemStack> newlootcontext(BlockState state, ServerWorld worldIn,
                                     BlockPos pos, TileEntity tileEntityIn,
                                     Entity entityIn, ItemStack dank){
    ItemStack tool = Utils.getItemStackInSelectedSlot(dank);
    LootContext.Builder lootcontext$builder = (new LootContext.Builder(worldIn)).withRandom(worldIn.rand).withParameter(LootParameters.POSITION, pos).withParameter(LootParameters.TOOL, tool).withNullableParameter(LootParameters.THIS_ENTITY, entityIn).withNullableParameter(LootParameters.BLOCK_ENTITY, tileEntityIn);
    return state.getDrops(lootcontext$builder);
  }

}
