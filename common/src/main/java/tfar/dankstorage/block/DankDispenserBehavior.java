package tfar.dankstorage.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import tfar.dankstorage.blockentity.CommonDockBlockEntity;

public class DankDispenserBehavior implements DispenseItemBehavior {

    @Override
    public ItemStack dispense(BlockSource pointer, ItemStack stack) {
        ServerLevel world = pointer.level();
        BlockPos blockPos = pointer.pos().relative(pointer.state().getValue(DispenserBlock.FACING));
        BlockState state = world.getBlockState(blockPos);
        if (state.getBlock() instanceof CDockBlock && state.getValue(CDockBlock.TIER) == 0) {
            insertDank(world, blockPos, stack);
            return ItemStack.EMPTY;
        } else if (state.getBlock() instanceof CDockBlock) {
            ItemStack old = removeDank(world, blockPos);
            insertDank(world, blockPos, stack);
            return old;
        }
        return stack;
    }

    public ItemStack removeDank(ServerLevel world, BlockPos pos) {
        CommonDockBlockEntity dockBlockEntity = (CommonDockBlockEntity) world.getBlockEntity(pos);
        return dockBlockEntity.removeDankWithoutItemSpawn();
    }

    public void insertDank(ServerLevel world, BlockPos pos, ItemStack stack) {
        CommonDockBlockEntity dockBlockEntity = (CommonDockBlockEntity) world.getBlockEntity(pos);
        dockBlockEntity.addDank(stack);
    }
}
