package tfar.dankstorage.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import tfar.dankstorage.blockentity.DockBlockEntity;

public class DankDispenserBehavior implements DispenseItemBehavior {

    @Override
    public ItemStack dispense(BlockSource pointer, ItemStack stack) {
        ServerLevel world = pointer.getLevel();
        BlockPos blockPos = pointer.getPos().relative(pointer.getBlockState().getValue(DispenserBlock.FACING));
        BlockState state = world.getBlockState(blockPos);
        if (state.getBlock() instanceof DockBlock && state.getValue(DockBlock.TIER) == 0) {
            insertDank(world, blockPos, stack);
            return ItemStack.EMPTY;
        } else if (state.getBlock() instanceof DockBlock) {
            ItemStack old = removeDank(world, blockPos);
            insertDank(world, blockPos, stack);
            return old;
        }
        return stack;
    }

    public ItemStack removeDank(ServerLevel world, BlockPos pos) {
        DockBlockEntity dockBlockEntity = (DockBlockEntity) world.getBlockEntity(pos);
        return dockBlockEntity.removeDankWithoutItemSpawn();
    }

    public void insertDank(ServerLevel world, BlockPos pos, ItemStack stack) {
        DockBlockEntity dockBlockEntity = (DockBlockEntity) world.getBlockEntity(pos);
        dockBlockEntity.addDank(stack);
    }
}
